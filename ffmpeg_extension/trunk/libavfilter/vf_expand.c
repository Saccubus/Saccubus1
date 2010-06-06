/*
 * video expand filter (alternative to pad syntax)
 * copyright (c) 2008 Ryo Hirafuji <http://ledyba.ddo.jp/>
 *
 * This file is part of FFmpeg.
 *
 * FFmpeg is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * FFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with FFmpeg; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

#include <stdio.h>

#include "avfilter.h"
#define INDEX_X 0
#define INDEX_Y 1
#define INDEX_MAX 2

typedef struct{
	int size[INDEX_MAX];
	int offset[INDEX_MAX];
    int shift[INDEX_MAX];

    int osd; ///< checked, but not used in this version.
    double aspect;
    int round;

    int bpp;                ///< bytes per pixel
    int is_yuv;
} ExpandContext;

static int init(AVFilterContext *ctx, const char *args, void *opaque)
{
    ExpandContext *expand = ctx->priv;
    int i;

    /* default parameters */
	for(i=0;i<INDEX_MAX;i++){
	    expand->offset[i] =  -1;
	    expand->size[i] = -1;
	    expand->shift[i] = 0;
	}
    expand->osd = 0;
    expand->aspect = 0.0f;
    expand->round = 0;

    expand->bpp = 0;
    expand->is_yuv = 0;

    if(args){
        int length = strlen(args);
        char* osd_tmp = av_malloc(length);
        char* aspect_tmp = av_malloc(length);
        if(!osd_tmp || !aspect_tmp){
	        av_log(ctx, AV_LOG_ERROR, "Failed to malloc.\n");
        }

        sscanf(args,"%d:%d:%d:%d:%255[^:]:%255[^:]:%d",
        		&expand->size[INDEX_X],&expand->size[INDEX_Y],&expand->offset[INDEX_X],&expand->offset[INDEX_Y],
        		osd_tmp,aspect_tmp,&expand->round
        	);

        if(osd_tmp && strlen(osd_tmp) > 0){ //checked, but not used in this version.
            if(!strncmp(osd_tmp,"true",4)){
                expand->osd = 1;
            }else{
                expand->osd = atoi(osd_tmp);
            }
        }

        if(aspect_tmp && strlen(aspect_tmp) > 0){
            char* cp = strchr(aspect_tmp, '/');
            if(cp){ // rational
                AVRational rat;
                char* cpp;
                rat.num = strtol(aspect_tmp, &cpp, 10);
                if(cpp != aspect_tmp || cpp == cp){
                    rat.den = strtol(cp+1, &cpp, 10);
                }else{
                    rat.num = 0;
                }
                if(rat.num && rat.den){
                    double eval = ((double)rat.num) / rat.den;
                    if(eval > 0.0f){
                        expand->aspect = eval;
                    }
                }
            }else{ // double
                double eval = strtod(aspect_tmp, 0);
                if(eval > 0.0f){
                    expand->aspect = eval;
                }
            }
        }

        av_log(ctx, AV_LOG_INFO, "Expand: %dx%d , (%d,%d) , osd: %d, aspect: %lf, round: %d\n",
        expand->size[INDEX_X], expand->size[INDEX_Y], expand->offset[INDEX_X], expand->offset[INDEX_Y], expand->osd, expand->aspect, expand->round);

        av_free(osd_tmp);
        av_free(aspect_tmp);
    }

    return 0;
}

static int query_formats(AVFilterContext *ctx){
    avfilter_set_common_formats(ctx,avfilter_make_format_list(30, // out of 38
         PIX_FMT_YUV420P,
         PIX_FMT_YUV422P,
         PIX_FMT_YUV444P,
         PIX_FMT_YUV410P,
         PIX_FMT_YUV411P,
         PIX_FMT_YUV440P,
         PIX_FMT_YUVJ420P,
         PIX_FMT_YUVJ422P,
         PIX_FMT_YUVJ444P,
         PIX_FMT_YUVJ440P,
         PIX_FMT_YUVA420P,
         PIX_FMT_NV12,
         PIX_FMT_NV21,
         PIX_FMT_RGB24,
         PIX_FMT_BGR24,
         PIX_FMT_RGB32,
         PIX_FMT_BGR32,
         PIX_FMT_RGB32_1,
         PIX_FMT_BGR32_1,
         PIX_FMT_GRAY16BE,
         PIX_FMT_GRAY16LE,
         PIX_FMT_BGR555,
         PIX_FMT_BGR565,
         PIX_FMT_RGB555,
         PIX_FMT_RGB565,
         //PIX_FMT_YUYV422, // not supported.
         //PIX_FMT_UYVY422, // not supported.
         //PIX_FMT_UYYVYY411, // not supported.
         PIX_FMT_RGB8,
         PIX_FMT_BGR8,
         PIX_FMT_RGB4_BYTE,
         PIX_FMT_BGR4_BYTE,
         PIX_FMT_GRAY8
         //PIX_FMT_RGB4, //not supported
         //PIX_FMT_BGR4, //not supported
         //PIX_FMT_MONOWHITE, // not supported
         //PIX_FMT_MONOBLACK, // not supported
         //PIX_FMT_PAL8, // not supported
    ));
    return 0;
}


static int config_input(AVFilterLink *link)
{
    ExpandContext *expand = link->dst->priv;
    int i;
    int size[INDEX_MAX];

    size[INDEX_X] = link->w;
    size[INDEX_Y] = link->h;

	for(i=0;i<INDEX_MAX;i++){
	    if (expand->size[i] == -1){
	        expand->size[i]=size[i];
	    } else if (expand->size[i] < -1){
	        expand->size[i]=size[i] - expand->size[i];
	    } else if (expand->size[INDEX_X] < size[i]){
	        expand->size[i]=size[i];
	    }
	}

    if (expand->aspect > 0.0f) {
        if (expand->size[INDEX_Y] < (expand->size[INDEX_X] / expand->aspect)) {
            expand->size[INDEX_Y] = (expand->size[INDEX_X] / expand->aspect) + 0.5;
        } else {
            expand->size[INDEX_X] = (expand->size[INDEX_Y] * expand->aspect) + 0.5;
        }
    }

	for(i=0;i<INDEX_MAX;i++){
	    if (expand->round > 1) {
	        expand->size[i] = (1+(expand->size[i]-1)/expand->round)*expand->round;
	    }
	    if(expand->offset[i] < 0 || (expand->offset[i]+size[i]) > expand->size[i]){
	        expand->offset[i] = (expand->size[INDEX_X] - size[i])>>1;
	    }
	}

    avcodec_get_chroma_sub_sample(link->format, &expand->shift[INDEX_X], &expand->shift[INDEX_Y]);
    for(i=0;i<INDEX_MAX;i++){
	    expand->offset[i] &= ~((1 << expand->shift[i]) - 1);
	    expand->size[i] &= ~((1 << expand->shift[i]) - 1);
    }

    switch(link->format) {
        case PIX_FMT_YUV420P:
        case PIX_FMT_YUV422P:
        case PIX_FMT_YUV444P:
        case PIX_FMT_YUV410P:
        case PIX_FMT_YUV411P:
        case PIX_FMT_YUV440P:
        case PIX_FMT_YUVJ420P:
        case PIX_FMT_YUVJ422P:
        case PIX_FMT_YUVJ444P:
        case PIX_FMT_YUVJ440P:
        case PIX_FMT_YUVA420P:
        case PIX_FMT_NV12:
        case PIX_FMT_NV21:
            expand->is_yuv = 1;
        case PIX_FMT_RGB8:
        case PIX_FMT_BGR8:
        case PIX_FMT_RGB4_BYTE:
        case PIX_FMT_BGR4_BYTE:
        case PIX_FMT_GRAY8:
            expand->bpp = 1;
            break;
        case PIX_FMT_RGB24:
        case PIX_FMT_BGR24:
            expand->bpp = 3;
            break;
        case PIX_FMT_RGB32:
        case PIX_FMT_BGR32:
        case PIX_FMT_RGB32_1:
        case PIX_FMT_BGR32_1:
            expand->bpp = 4;
            break;
        case PIX_FMT_GRAY16BE:
        case PIX_FMT_GRAY16LE:
        case PIX_FMT_BGR555:
        case PIX_FMT_BGR565:
        case PIX_FMT_RGB555:
        case PIX_FMT_RGB565:
            expand->bpp = 2;
            break;
        // not supported.
        //case PIX_FMT_YUYV422:
        //case PIX_FMT_UYVY422:
        //case PIX_FMT_UYYVYY411:
        //case PIX_FMT_RGB4:
        //case PIX_FMT_BGR4:
        //case PIX_FMT_MONOWHITE:
        //case PIX_FMT_MONOBLACK:
        //case PIX_FMT_PAL8:
        default: // invalid or not supported format
            return -1;
    }

    return 0;
}

static int config_output(AVFilterLink *link)
{
    ExpandContext *expand = link->src->priv;

    link->w = expand->size[INDEX_X];
    link->h = expand->size[INDEX_Y];

    return 0;
}

static void start_frame(AVFilterLink *link, AVFilterPicRef *picref)
{
    AVFilterLink *out = link->dst->outputs[0];
    out->outpic       = avfilter_get_video_buffer(out, AV_PERM_WRITE);
    out->outpic->pts  = picref->pts;
    avfilter_start_frame(out, avfilter_ref_pic(out->outpic, ~0));
}

static void draw_slice(AVFilterLink *link, int y, int h)
{
    ExpandContext *expand = link->dst->priv;
    AVFilterPicRef *outpic = link->dst->outputs[0]->outpic;
    AVFilterPicRef *inpic = link->cur_pic;
    int i;
    int is_first = (y <= 0);
    int is_end = (y+h >= inpic->h);

    for(i=0;i<4;i++) {
        if(outpic->data[i]) {
            int j;
            char* out_buff = outpic->data[i];
            const char* in_buff  = inpic->data[i];

            int copy_length;
            int y_add;
            int padcolor;
            int x_shift,y_shift;

            if(!expand->is_yuv || i == 3){ // not YUV, or alpha channel of YUVA
                padcolor = 0;
                x_shift = y_shift = 0;
            }else{
                padcolor = (i == 0) ? 16 : 128;
                x_shift = (i == 0) ? 0 : expand->shift[INDEX_X];
                y_shift = (i == 0) ? 0 : expand->shift[INDEX_Y];
            }

            copy_length = (inpic->w >> x_shift) * expand->bpp;
            y_add = 1<<y_shift;

            if(is_first){
                int size = (expand->offset[INDEX_Y] >> y_shift) * outpic->linesize[i];
                memset(out_buff,padcolor,size);
                out_buff += size;
            }else{
                int y_skip = expand->offset[INDEX_Y] >> y_shift;
                out_buff += outpic->linesize[i] * y_skip;
                in_buff += inpic->linesize[i] * y_skip;
            }

            for(j=0;j<h;j+=y_add){
                int size,total_size = 0;
                size = (expand->offset[INDEX_X] >> x_shift) * expand->bpp;
                memset(out_buff,padcolor,size);
                out_buff += size;
                total_size += size;

                memcpy(out_buff,in_buff,copy_length);
                out_buff += copy_length;
                total_size += copy_length;

                size = outpic->linesize[i]-total_size;
                memset(out_buff,padcolor,size);
                out_buff += size;

                in_buff += inpic->linesize[i];
            }

            if(is_end){
                memset(out_buff,padcolor,((outpic->h-expand->offset[INDEX_Y]-inpic->h) >> y_shift) * outpic->linesize[i]);
            }

        }
    }
    if(is_first && is_end){
        avfilter_draw_slice(link->dst->outputs[0], 0, outpic->h);
    }else if(is_first){
        avfilter_draw_slice(link->dst->outputs[0], 0, expand->offset[INDEX_Y] + h);
    }else if(is_end){
        avfilter_draw_slice(link->dst->outputs[0], expand->offset[INDEX_Y] + y, outpic->h - expand->offset[INDEX_Y] - y);
    }else{
        avfilter_draw_slice(link->dst->outputs[0], expand->offset[INDEX_Y] + y, h);
    }
}

AVFilter avfilter_vf_expand = {
    .name      = "expand",
    .priv_size = sizeof(ExpandContext),

    .init      = init,
    .query_formats   = query_formats,

    .inputs    = (AVFilterPad[]) {{ .name            = "default",
                                    .type            = CODEC_TYPE_VIDEO,
                                    .start_frame     = start_frame,
                                    .draw_slice      = draw_slice,
                                    .config_props    = config_input, },
                                  { .name = NULL}},
    .outputs   = (AVFilterPad[]) {{ .name            = "default",
                                    .type            = CODEC_TYPE_VIDEO,
                                    .config_props    = config_output, },
                                  { .name = NULL}},
};

