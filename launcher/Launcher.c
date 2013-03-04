/*
 * Windows�p�v���O���������`��
 * copyright (c) 2008 �Ձi�v�T�C�j
 *
 * �u������΂��v�{�̂�exe�o�R�œ��������߂̃����`���ł��B
 *
 * ���̃t�@�C���́u������΂��v�̈ꕔ�ł���A
 * ���̃\�[�X�R�[�h��GPL���C�Z���X�Ŕz�z����܂��ł��B
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
					/*����������ꍇ�́A�A�����Ď��s�B*/

					//�f�[�^�����W�B
					const char* cmd_base = CMD_RUN;
					int cmd_base_length = strlen(cmd_base);
					const char* cmd_add = szCmdLine;
					int cmd_add_length = strlen(cmd_add);
					//�������m��
					char* call_cmd_line = (char *)malloc(cmd_base_length+cmd_add_length+2);
					//Java�p�̕������R�s�[�Bstrcat�Ƃ��͂���܂�g�������Ȃ��B
					memcpy(call_cmd_line,cmd_base,cmd_base_length);
					//�󔒂�ǉ�
					call_cmd_line[cmd_base_length] = ' ';
					//������ǉ�
					memcpy(call_cmd_line+cmd_base_length+1,cmd_add,cmd_add_length);
					//�Ō��\0��Y�ꂸ�ɁB
					call_cmd_line[cmd_base_length+cmd_add_length+1] = '\0';
					//�R�}���h���s�B
					ret = doCmd(call_cmd_line,TRUE,CMD_LOG);
					//�Y�ꂸ�ɊJ���B
					free(call_cmd_line);
				}else{/*�����������ꍇ�͕��ʂɎ��s*/
					ret = doCmd(CMD_RUN,TRUE,CMD_LOG);
				}
			}else{
				MessageBoxA(NULL,"Java���C���X�g�[������Ă��Ȃ��悤�ł��B","�G���[",MB_OK | MB_ICONERROR);
				ret = -1;
			}
			return ret;
}

int doCmd(char* command,int show_msg,const char* log_name){
	int ret = 0;

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
	int code = CreateProcessA(
	    NULL,						// ���s�t�@�C����
	    command,					// �R�}���h���C���p�����[�^
	    NULL,						// �v���Z�X�̕ی쑮��
	    NULL,						// �X���b�h�̕ی쑮��
	    TRUE,						// �I�u�W�F�N�g�n���h���p���̃t���O
	    DETACHED_PROCESS | 
		CREATE_NEW_PROCESS_GROUP | 
		NORMAL_PRIORITY_CLASS |
		CREATE_NO_WINDOW,		// �����t���O
	    NULL,						// ���ϐ����ւ̃|�C���^
	    NULL,						// �N�����J�����g�f�B���N�g��
	    &startup_info,				// �E�B���h�E�\���ݒ�
	    &process_info				// �v���Z�X�E�X���b�h�̏��
	);
	if(code == 0){
		ret = -1;
		if(show_msg){
			char msg[100];
			int error_code = FormatMessageA(
				FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM,	// ����t���O
				0,																// ���b�Z�[�W��`�ʒu
				GetLastError(),													// ���b�Z�[�WID
				LANG_USER_DEFAULT,												// ����ID
				(LPSTR)&msg,													// �o�b�t�@�̃A�h���X
				0,																// �o�b�t�@�̃T�C�Y
				0																// �}����̔z��̃A�h���X
			);
			if(error_code == 0){
				MessageBoxA(NULL,"���炩�̃G���[���������܂����B","�G���[",MB_OK | MB_ICONERROR);
			}else{
				MessageBoxA(NULL,msg, "�G���[", MB_ICONERROR|MB_OK);
			}
			LocalFree(msg);
		}
	}else{
		/* //�߂�l���擾����ꍇ�̓R�����g�A�E�g����
		// �I������܂ő҂�
		WaitForSingleObject(process_info.hProcess,INFINITE);
		//�߂�l���擾
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
