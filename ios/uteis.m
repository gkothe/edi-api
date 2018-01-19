// CalendarManager.m
#import "uteis.h"
#import <MessageUI/MFMailComposeViewController.h>
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <MediaPlayer/MediaPlayer.h>

@interface EdiApi () {
    MPMoviePlayerViewController *playerv;
    MPMoviePlayerController *player ;
}
@end

@implementation EdiApi

RCT_EXPORT_MODULE();


RCT_EXPORT_METHOD(publicarTwitter:(NSString *)msg call:(RCTResponseSenderBlock)callback)
{
    NSString *url = [NSString stringWithFormat:@"whatsapp://send?text=%@",[msg stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    NSURL *whatsappURL = [NSURL URLWithString:url];
    if ([[UIApplication sharedApplication] canOpenURL: whatsappURL]) {
        [[UIApplication sharedApplication] openURL: whatsappURL];
        callback(@[@"ok"]);
    } else {
        callback(@[@"Não instalado"]);

    }
}

RCT_EXPORT_METHOD(publicarWhatsApp:(NSString *)msg call:(RCTResponseSenderBlock)callback)
{
    NSString *urlWhats = [NSString stringWithFormat:@"twitter://post?message=%@",[msg stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    NSURL *whatsappURL = [NSURL URLWithString:urlWhats];
    if ([[UIApplication sharedApplication] canOpenURL: whatsappURL]) {
        [[UIApplication sharedApplication] openURL: whatsappURL];
        callback(@[@"ok"]);
    } else {
        callback(@[@"Não instalado"]);
    }
}
-(void)sms:(NSString*)msg{
//    MFMessageComposeViewController *controller = [[MFMessageComposeViewController alloc] init] ;
//    if([MFMessageComposeViewController canSendText])
//    {
//        controller.body = msg;
//        controller.messageComposeDelegate = self;
//        //        [nav presentModalViewController:controller animated:YES];
////        [nav presentViewController: controller animated:YES completion:nil];
//    }
}

RCT_EXPORT_METHOD(playVideo:(NSString *)url )
{
    NSLog(@"%@",url);

     UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
    UIViewController *nova = [[UIViewController alloc]init];
    
    if(!url || [url length]< 10 ||  ![url hasPrefix:@"http"]){
        [ [UIApplication sharedApplication].keyWindow.rootViewController dismissViewControllerAnimated:YES completion:nil];
        return ;
    }
    @try {
        
        NSURL *video = [NSURL URLWithString:url];

        player = [[MPMoviePlayerController alloc] initWithContentURL:video];
        player.controlStyle = MPMovieControlStyleFullscreen;
        player.movieSourceType = MPMovieSourceTypeStreaming;
       
        [nova.view addSubview:player.view];
        
        [player.view setFrame:nova.view.frame];
        [player prepareToPlay];
        [player play];
     } @catch (NSException *exception) {
         NSLog(@"%@",exception.description);

         [ [UIApplication sharedApplication].keyWindow.rootViewController dismissViewControllerAnimated:YES completion:nil];

     }
    
    [topController presentViewController: nova animated:YES completion:nil];

    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(moviePlayBackDidFinish:)
                                                 name:MPMoviePlayerPlaybackDidFinishNotification
                                               object:player];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(moviePlayBackDidFinish:)
                                                 name:MPMoviePlayerDidExitFullscreenNotification
                                               object:player];
    
    UISwipeGestureRecognizer *recognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self
                                                                                     action:@selector(moviePlayBackDidFinish:)];
//    [recognizer setDirection:(UISwipeGestureRecognizerDirectionLeft)];
//    [nova.view addGestureRecognizer:recognizer];
    
//    recognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self
//                                                           action:@selector(moviePlayBackDidFinish:)];
    recognizer.delegate = self;
    [recognizer setDirection:(UISwipeGestureRecognizerDirectionDown)];
    [nova.view addGestureRecognizer:recognizer];
    
}
- (IBAction) moviePlayBackDidFinish:(id)sender{
    if(player){
        [player stop];
    }
    [ [UIApplication sharedApplication].keyWindow.rootViewController dismissViewControllerAnimated:YES completion:nil];
 }

- (UIViewController*) topMostController
{
//    return [[[[UIApplication sharedApplication] keyWindow] subviews] lastObject];
    UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
    while (topController.presentedViewController) {
        topController = topController.presentedViewController;
    }
    return topController;
}

RCT_EXPORT_METHOD(abrirMaps:(NSString *)endereco )
{
    NSString *url = [NSString stringWithFormat:@"http://maps.google.com/maps?q=%@",[endereco stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    NSURL *whatsappURL = [NSURL URLWithString:url];
    if ([[UIApplication sharedApplication] canOpenURL: whatsappURL]) {
        [[UIApplication sharedApplication] openURL: whatsappURL];
       
    }

}


@end

