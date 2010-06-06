/*
 * MacOSX用Cookie取得／設定プログラム
 * copyright (c) 2010 ψ（プサイ）
 *
 * Safariのクッキーを取得して表示するプログラムです。
 *
 * このファイルは「さきゅばす」の一部であり、
 * このソースコードはGPLライセンスで配布されますです。
 */

//ニコニコ動画URL
#define NICO_URL "http://www.nicovideo.jp/"

int setCookie(NSHTTPCookieStorage* storage,const char* str){
	NSString* cookie_str = [[NSString alloc] initWithUTF8String:str];
	NSArray* item_array = [cookie_str componentsSeparatedByString:@"; "];
	NSMutableDictionary* prop = [NSMutableDictionary dictionary];
	[prop setObject:(@NICO_URL) forKey:NSHTTPCookieOriginURL];
	for(int i=0;i<[item_array count];i++){
		NSString* item = [item_array objectAtIndex:i];
		unsigned long idx = [item rangeOfString: @"="].location;
		if(idx != NSNotFound){
			NSString* key = [item substringToIndex: idx];
			NSString* value = [item substringFromIndex: idx+1];
			if([key isEqualToString: @"expires"]){
				[prop setObject:[NSDate dateWithNaturalLanguageString:value] forKey:NSHTTPCookieExpires];
			}else if ([key isEqualToString: @"path"]) {
				[prop setObject:value forKey:NSHTTPCookiePath];
			}else if([key isEqualToString:@"domain"]){
				[prop setObject:value forKey:NSHTTPCookieDomain];				
			}else if(i == 0) {
				[prop setObject:key forKey:NSHTTPCookieName];
				[prop setObject:value forKey:NSHTTPCookieValue];
			}else{
				fprintf(stderr, "Ignoring: %s=%s",[key UTF8String],[value UTF8String]);
			}
		}else if([item isEqualToString:@"secure"]){
			[prop setObject:@"secure" forKey: NSHTTPCookieSecure];
		}else{
			fprintf(stderr, "Ignoring: %s\n",[item UTF8String]);
		}
	}
	NSHTTPCookie* cookie = [NSHTTPCookie cookieWithProperties: prop];
	[storage setCookieAcceptPolicy: NSHTTPCookieAcceptPolicyAlways];
	[storage setCookie:cookie];
	return 0;
}

int getCookie(NSHTTPCookieStorage* storage){
	NSArray * cookies = [storage cookiesForURL: [NSURL URLWithString:@NICO_URL]];
	for(int i=0;i<[cookies count];i++){
		NSHTTPCookie* cookie = [cookies objectAtIndex:i];
		if ([@"/" isEqualToString: [cookie path]]) {
			NSString* name = [cookie name];
			NSString* value = [cookie value];
			if(name == nil || value == nil){
				fprintf(stderr, "Invalid cookie.\n");
				return -1;
			}
			fprintf(stdout, "%s=%s",[name UTF8String],[value UTF8String]);
			NSDate* date = [cookie expiresDate];
			if(date != nil){
				NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
				NSLocale *locale = [[NSLocale alloc] initWithLocaleIdentifier:@"en_US"];
				[formatter setLocale:locale];
				[formatter setTimeZone:[NSTimeZone timeZoneForSecondsFromGMT:0]];
				[formatter setDateFormat:@"EEE, dd-MMM-yyyy HH:mm:ss"];
				NSString* formatted = [formatter stringFromDate:date];
				fprintf(stdout, "; expires=%s GMT",[formatted UTF8String]);
			} 
			NSString* path = [cookie path];
			if (path != nil) {
				fprintf(stdout, "; path=%s",[path UTF8String]);
			}
			NSString* domain = [cookie domain];
			if (domain != nil) {
				fprintf(stdout, "; domain=%s", [domain UTF8String]);
			}
			if ([cookie isSecure]) {
				fprintf(stdout, "; secure");
			}
			fprintf(stdout, "\n");
			return 0;
		}
	}
	fprintf(stderr, "failed to read Cookies.\n");
	return -1;
}

int main (int argc, const char * argv[]) {
    NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];

    // insert code here...

	int ret = 0;
	NSHTTPCookieStorage* storage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
	if(storage == nil){
		fprintf(stderr, "failed to get Cookie Storage.\n");
		return -1;
	}
	if(argc > 1){ /* セットすべきクッキーが存在する */
		setCookie(storage, argv[1]);
	}else{
		getCookie(storage);
	}
    [pool drain];
    return ret;
}
