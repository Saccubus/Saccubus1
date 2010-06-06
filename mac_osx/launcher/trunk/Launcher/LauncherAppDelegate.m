//
//  LauncherAppDelegate.m
//  Launcher
//
//  Created by g050267 on 10/04/28.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "LauncherAppDelegate.h"
#define CMD_CHECK "java"
#define CMD_RUN "java -jar Saccubus.jar"

@implementation LauncherAppDelegate

@synthesize window;

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification {
	// Insert code here to initialize your application 
	if(system(CMD_CHECK) != 0){
		NSAlert* alert =[
						 NSAlert alertWithMessageText:@"警告"
						 defaultButton:@"OK"
						 alternateButton:nil
						 otherButton:nil
						 informativeTextWithFormat:@"Javaがインストールされていないようです。"];
		[alert runModal];
		exit(-1);
	}
	NSArray* cmdParam = [[NSProcessInfo processInfo] arguments];
	NSLog(@"%@", cmdParam);
}

@end
