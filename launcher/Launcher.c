/*
 * Windows用プログラムランチャ
 * copyright (c) 2008 ψ（プサイ）
 *
 * 「さきゅばす」本体をexe経由で動かすためのランチャです。
 *
 * このファイルは「さきゅばす」の一部であり、
 * このソースコードはGPLライセンスで配布されますです。
 */
#include <stdlib.h>
#include <stdio.h>
#include <windows.h>

#define CMD_CHECK "java.exe"
#define CMD_RUN "java -jar Saccubus.jar"
#define CMD_LOG "[log]frontend.txt"

int doCmd(char* command,int show_msg,const char* log_name);

int WINAPI WinMain (HINSTANCE hInstance, 
			HINSTANCE hPrevInstance, 
			PSTR szCmdLine, 
			int iCmdShow){
			int ret;
			if(doCmd(CMD_CHECK,FALSE,NULL) == 0){
				if(szCmdLine && strlen(szCmdLine) > 0){
					/*引数がある場合は、連結して実行。*/

					//データを収集。
					const char* cmd_base = CMD_RUN;
					int cmd_base_length = strlen(cmd_base);
					const char* cmd_add = szCmdLine;
					int cmd_add_length = strlen(cmd_add);
					//メモリ確保
					char* call_cmd_line = (char *)malloc(cmd_base_length+cmd_add_length+2);
					//Java用の部分をコピー。strcatとかはあんまり使いたくない。
					memcpy(call_cmd_line,cmd_base,cmd_base_length);
					//空白を追加
					call_cmd_line[cmd_base_length] = ' ';
					//引数を追加
					memcpy(call_cmd_line+cmd_base_length+1,cmd_add,cmd_add_length);
					//最後の\0を忘れずに。
					call_cmd_line[cmd_base_length+cmd_add_length+1] = '\0';
					//コマンド実行。
					ret = doCmd(call_cmd_line,TRUE,CMD_LOG);
					//忘れずに開放。
					free(call_cmd_line);
				}else{/*引数が無い場合は普通に実行*/
					ret = doCmd(CMD_RUN,TRUE,CMD_LOG);
				}
			}else{
				MessageBoxA(NULL,"Javaがインストールされていないようです。","エラー",MB_OK | MB_ICONERROR);
				ret = -1;
			}
			return ret;
}

int doCmd(char* command,int show_msg,const char* log_name){
	int ret = 0;
	int code;

	STARTUPINFOA startup_info;
	PROCESS_INFORMATION process_info;
	HANDLE _hnd_out = INVALID_HANDLE_VALUE;
	HANDLE hnd_out = INVALID_HANDLE_VALUE;

	process_info.hProcess = NULL;

	memset(&startup_info, 0, sizeof(STARTUPINFO));
	startup_info.cb = sizeof(STARTUPINFO);

	if(log_name){
		_hnd_out = CreateFileA(
			log_name,
			GENERIC_WRITE,
			FILE_SHARE_WRITE,
			NULL,
			CREATE_ALWAYS,
			FILE_ATTRIBUTE_NORMAL,
			NULL
		);
		if(_hnd_out != INVALID_HANDLE_VALUE){
			DuplicateHandle(GetCurrentProcess(), _hnd_out, GetCurrentProcess(),&hnd_out, 0, 1, DUPLICATE_SAME_ACCESS);
			if(hnd_out != INVALID_HANDLE_VALUE){
				startup_info.dwFlags = STARTF_USESTDHANDLES;
				startup_info.hStdOutput = hnd_out;
				startup_info.hStdError = hnd_out;
			}
		}
	}
	code = CreateProcessA(
	    NULL,						// 実行ファイル名
	    command,					// コマンドラインパラメータ
	    NULL,						// プロセスの保護属性
	    NULL,						// スレッドの保護属性
	    TRUE,						// オブジェクトハンドル継承のフラグ
	    DETACHED_PROCESS | 
		CREATE_NEW_PROCESS_GROUP | 
		NORMAL_PRIORITY_CLASS |
		CREATE_NO_WINDOW,		// 属性フラグ
	    NULL,						// 環境変数情報へのポインタ
	    NULL,						// 起動時カレントディレクトリ
	    &startup_info,				// ウィンドウ表示設定
	    &process_info				// プロセス・スレッドの情報
	);
	if(code == 0){
		ret = -1;
		if(show_msg){
			char *msg;
			int error_code = FormatMessageA(
				FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM,	// 動作フラグ
				0,																// メッセージ定義位置
				GetLastError(),													// メッセージID
				LANG_USER_DEFAULT,												// 言語ID
				(LPSTR)&msg,													// バッファのアドレス
				0,																// バッファのサイズ
				0																// 挿入句の配列のアドレス
			);
			if(error_code == 0){
				MessageBoxA(NULL,"何らかのエラーが発生しました。","エラー",MB_OK | MB_ICONERROR);
			}else{
				MessageBoxA(NULL,msg, "エラー", MB_ICONERROR|MB_OK);
			}
			LocalFree(msg);
		}
	}else{
		/* 
		//戻り値を取得する場合はコメントアウト解除
		// 終了するまで待つ
		WaitForSingleObject(process_info.hProcess,INFINITE);
		//戻り値を取得
		DWORD exit_code;
		GetExitCodeProcess(process_info.hProcess, &exit_code);
		ret = exit_code;
		*/
		if(_hnd_out != INVALID_HANDLE_VALUE){
			CloseHandle(_hnd_out);
		}
		if(hnd_out != INVALID_HANDLE_VALUE){
			CloseHandle(hnd_out);
		}
		CloseHandle(process_info.hThread);
		CloseHandle(process_info.hProcess);

	}
 	return ret;
}
