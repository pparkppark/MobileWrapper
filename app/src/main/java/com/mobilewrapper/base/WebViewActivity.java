package com.mobilewrapper.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.mobilewrapper.base.common.BaseActivity;
import com.mobilewrapper.base.communication.retrofit.beans.Banner;
import com.mobilewrapper.base.gcm.beans.PushMessage;
import com.mobilewrapper.base.widget.EndingBanner;
import com.mobilewrapper.base.widget.StartBanner;
import com.mobilewrapper.base.widget.TopBanner;

import java.net.URISyntaxException;
import java.util.Hashtable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ppark on 2015-08-20.
 */
public class WebViewActivity extends BaseActivity {

    @Bind(R.id.webViewBack)
    Button webViewBack;
    @Bind(R.id.webViewForward)
    Button webViewForward;
    @Bind(R.id.webViewHome)
    Button webViewHome;
    @Bind(R.id.webViewRefresh)
    Button webViewRefresh;
    @Bind(R.id.pushBox)
    Button pushBox;
    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.topBannerLayout)
    TopBanner topBannerLayout;

    private boolean bIsEndingBannerCanceled = false;
    private boolean bHistoryClear = false;
    private long lastPressedTime = 0;
    private static int BACK_PREIOD = 2000;

    private static final int DIALOG_PROGRESS_WEBVIEW = 0;
    private static final int DIALOG_PROGRESS_MESSAGE = 1;
    private static final int DIALOG_ISP = 2;
    private static final int DIALOG_CARDAPP = 3;
    private static String DIALOG_CARDNM = "";
    private AlertDialog alertIsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        startBannerSetting();
        topBannerSetting();

        webViewSetting();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        PushMessage msg = (PushMessage) getIntent().getSerializableExtra(WrapperApplication.EXTRA_SERIAL_PUSHMSG);
        if(msg != null) {
            getIntent().removeExtra(WrapperApplication.EXTRA_SERIAL_PUSHMSG);
            webView.loadUrl(msg.getLinkUrl());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case RESULT_OK :
            {
                String url = data.getStringExtra("pushUrl");
                if(url != null || !url.equals("")) {
                    getIntent().removeExtra("pushUrl");
                    webView.loadUrl(url);
                }
            }
                break;
        }
    }

    private void startBannerSetting() {
        Banner banner = WrapperApplication.getInitialize().getStartBanner();

        if(banner != null) {
            new StartBanner(this, banner);
        }
    }

    private void topBannerSetting() {
        Banner bannerTop = WrapperApplication.getInitialize().getTopBanner();

        if(bannerTop != null) {
            topBannerLayout.init(bannerTop);
        }
    }

    private void webViewSetting() {
        webView.setWebViewClient(new CommonWebViewClient());
        webView.setWebChromeClient(new CommonChromeClient());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.loadUrl(WrapperApplication.webViewHome);
    }

    @OnClick(R.id.topBannerImage)
    void onTopBannerClick() {

    }

    @OnClick(R.id.topBannerClose)
    void onTopBannerCloseClick() {

    }

    @OnClick(R.id.webViewBack)
    void onWebBackClick() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    @OnClick(R.id.webViewForward)
    void onWebForwordClick() {
        if (webView.canGoForward()) {
            webView.goForward();
        }
    }

    @OnClick(R.id.webViewHome)
    void onWebHomeClick() {
        webView.loadUrl(WrapperApplication.webViewHome);
        bHistoryClear = true;
    }

    @OnClick(R.id.webViewRefresh)
    void onWebRefreshClick() {
        webView.loadUrl(webView.getUrl());
    }

    @OnClick(R.id.pushBox)
    void onPushBoxClick() {
        Intent intent = new Intent(this, PushBoxActivity.class);
        startActivityWithTransitionForResult(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK : {
                if (webView.canGoBack()) {
                    onWebBackClick();
                    return true;
                }
                else {
                    Banner banner = WrapperApplication.getInitialize().getEndingBanner();

                    if (banner != null) {
                        bIsEndingBannerCanceled = false;

                        EndingBanner endingBanner = new EndingBanner(this, banner);
                        endingBanner.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if(bIsEndingBannerCanceled == false) {
                                    finish();
                                }
                            }
                        });
                        endingBanner.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                bIsEndingBannerCanceled = true;
                            }
                        });
                        endingBanner.show();

                    } else {
                        if (event.getDownTime() - lastPressedTime < BACK_PREIOD) {
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.toast_doubleTapToExit, Toast.LENGTH_SHORT).show();
                            lastPressedTime = event.getEventTime();
                        }
                        return true;
                    }
                }
            }
            break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("unused")
    private AlertDialog getCardInstallAlertDialog(final String coCardNm){

        final Hashtable<String, String> cardNm = new Hashtable<String, String>();
        cardNm.put("HYUNDAE", "현대 앱카드");
        cardNm.put("SAMSUNG", "삼성 앱카드");
        cardNm.put("LOTTE",   "롯데 앱카드");
        cardNm.put("SHINHAN", "신한 앱카드");
        cardNm.put("KB", 	  "국민 앱카드");
        cardNm.put("HANASK",  "하나SK 통합안심클릭");
        //cardNm.put("SHINHAN_SMART",  "Smart 신한앱");

        final Hashtable<String, String> cardInstallUrl = new Hashtable<String, String>();
        cardInstallUrl.put("HYUNDAE", "market://details?id=com.hyundaicard.appcard");
        cardInstallUrl.put("SAMSUNG", "market://details?id=kr.co.samsungcard.mpocket");
        cardInstallUrl.put("LOTTE",   "market://details?id=com.lotte.lottesmartpay");
        cardInstallUrl.put("LOTTEAPPCARD",   "market://details?id=com.lcacApp");
        cardInstallUrl.put("SHINHAN", "market://details?id=com.shcard.smartpay");
        cardInstallUrl.put("KB", 	  "market://details?id=com.kbcard.cxh.appcard");
        cardInstallUrl.put("HANASK",  "market://details?id=com.ilk.visa3d");
        //cardInstallUrl.put("SHINHAN_SMART",  "market://details?id=com.shcard.smartpay");//여기 수정 필요!!2014.04.01

        AlertDialog alertCardApp =  new AlertDialog.Builder(WebViewActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("알림")
                .setMessage( cardNm.get(coCardNm) + " 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.")
                .setPositiveButton("설치", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String installUrl = cardInstallUrl.get(coCardNm);
                        Uri uri = Uri.parse(installUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        Log.d("<INIPAYMOBILE>","Call : "+uri.toString());
                        try{
                            startActivity(intent);
                        }catch (ActivityNotFoundException anfe) {
                            Toast.makeText(WebViewActivity.this, cardNm.get(coCardNm) + "설치 url이 올바르지 않습니다" , Toast.LENGTH_SHORT).show();
                        }
                        //finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(WebViewActivity.this, "(-1)결제를 취소 하셨습니다." , Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .create();

        return alertCardApp;

    }//end getCardInstallAlertDialog


    protected Dialog onCreateDialog(int id) {//ShowDialog
        switch(id){
            case DIALOG_PROGRESS_WEBVIEW:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("로딩중입니다. \n잠시만 기다려주세요.");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;

            case DIALOG_PROGRESS_MESSAGE:
                break;


            case DIALOG_ISP:

                alertIsp =  new AlertDialog.Builder(WebViewActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("알림")
                        .setMessage("모바일 ISP 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.")
                        .setPositiveButton("설치", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String ispUrl = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp";
                                webView.loadUrl(ispUrl);
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(WebViewActivity.this, "(-1)결제를 취소 하셨습니다." , Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        })
                        .create();

                return alertIsp;

            case DIALOG_CARDAPP :
                return getCardInstallAlertDialog(DIALOG_CARDNM);

        }//end switch

        return super.onCreateDialog(id);

    }//end onCreateDialog

    class CommonWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            /*
	    	 * URL별로 분기가 필요합니다. 어플리케이션을 로딩하는것과
	    	 * WEB PAGE를 로딩하는것을 분리 하여 처리해야 합니다.
	    	 * 만일 가맹점 특정 어플 URL이 들어온다면
	    	 * 조건을 더 추가하여 처리해 주십시요.
	    	 */

            if( !url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:") )
            {
                Intent intent;

                try{
                    Log.d("<INIPAYMOBILE>", "intent url : " + url);
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    Log.d("<INIPAYMOBILE>", "intent getDataString : " + intent.getDataString());
                    Log.d("<INIPAYMOBILE>", "intent getPackage : " + intent.getPackage() );

                } catch (URISyntaxException ex) {
                    Log.e("<INIPAYMOBILE>", "URI syntax error : " + url + ":" + ex.getMessage());
                    return false;
                }

                Uri uri = Uri.parse(intent.getDataString());
                intent = new Intent(Intent.ACTION_VIEW, uri);



                try {

                    startActivity(intent);

	    			/*가맹점의 사정에 따라 현재 화면을 종료하지 않아도 됩니다.
	    			    삼성카드 기타 안심클릭에서는 종료되면 안되기 때문에
	    			    조건을 걸어 종료하도록 하였습니다.*/
                    if( url.startsWith("ispmobile://"))
                    {
                        finish();
                    }

                }catch(ActivityNotFoundException e)
                {
                    Log.e("INIPAYMOBILE", "INIPAYMOBILE, ActivityNotFoundException INPUT >> " + url);
                    Log.e("INIPAYMOBILE", "INIPAYMOBILE, uri.getScheme()" + intent.getDataString());

                    //ISP
                    if( url.startsWith("ispmobile://"))
                    {
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_ISP);
                        return false;
                    }

                    //현대앱카드
                    else if( intent.getDataString().startsWith("hdcardappcardansimclick://"))
                    {
                        DIALOG_CARDNM = "HYUNDAE";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 현대앱카드설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }

                    //신한앱카드
                    else if( intent.getDataString().startsWith("shinhan-sr-ansimclick://"))
                    {
                        DIALOG_CARDNM = "SHINHAN";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 신한카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }

                    //삼성앱카드
                    else if( intent.getDataString().startsWith("mpocket.online.ansimclick://"))
                    {
                        DIALOG_CARDNM = "SAMSUNG";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 삼성카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }

                    //롯데 모바일결제
                    else if( intent.getDataString().startsWith("lottesmartpay://"))
                    {
                        DIALOG_CARDNM = "LOTTE";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 롯데모바일결제 설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }
                    //롯데앱카드(간편결제)
                    else if(intent.getDataString().startsWith("lotteappcard://"))
                    {
                        DIALOG_CARDNM = "LOTTEAPPCARD";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 롯데앱카드 설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }

                    //KB앱카드
                    else if( intent.getDataString().startsWith("kb-acp://"))
                    {
                        DIALOG_CARDNM = "KB";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, KB카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }

                    //하나SK카드 통합안심클릭앱
                    else if( intent.getDataString().startsWith("hanaansim://"))
                    {
                        DIALOG_CARDNM = "HANASK";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 하나카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }

	    			/*
	    			//신한카드 SMART신한 앱
	    			else if( intent.getDataString().startsWith("smshinhanansimclick://"))
	    			{
	    				DIALOG_CARDNM = "SHINHAN_SMART";
	    				Log.e("INIPAYMOBILE", "INIPAYMOBILE, Smart신한앱설치");
	    				view.loadData("<html><body></body></html>", "text/html", "euc-kr");
	    				showDialog(DIALOG_CARDAPP);
				        return false;
	    			}
	    			*/

                    /**
                     > 현대카드 안심클릭 droidxantivirusweb://
                     - 백신앱 : Droid-x 안드로이이드백신 - NSHC
                     - package name : net.nshc.droidxantivirus
                     - 특이사항 : 백신 설치 유무는 체크를 하고, 없을때 구글마켓으로 이동한다는 이벤트는 있지만, 구글마켓으로 이동되지는 않음
                     - 처리로직 : intent.getDataString()로 하여 droidxantivirusweb 값이 오면 현대카드 백신앱으로 인식하여
                     하드코딩된 마켓 URL로 이동하도록 한다.
                     */

                    //현대카드 백신앱
                    else if( intent.getDataString().startsWith("droidxantivirusweb"))
                    {
                        /*************************************************************************************/
                        Log.d("<INIPAYMOBILE>", "ActivityNotFoundException, droidxantivirusweb 문자열로 인입될시 마켓으로 이동되는 예외 처리: " );
                        /*************************************************************************************/

                        Intent hydVIntent = new Intent(Intent.ACTION_VIEW);
                        hydVIntent.setData(Uri.parse("market://search?q=net.nshc.droidxantivirus"));
                        startActivity(hydVIntent);

                    }


                    //INTENT:// 인입될시 예외 처리
                    else if( url.startsWith("intent://"))
                    {
                        /**

                         > 삼성카드 안심클릭
                         - 백신앱 : 웹백신 - 인프라웨어 테크놀러지
                         - package name : kr.co.shiftworks.vguardweb
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 신한카드 안심클릭
                         - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                         - package name : com.TouchEn.mVaccine.webs
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 농협카드 안심클릭
                         - 백신앱 : V3 Mobile Plus 2.0
                         - package name : com.ahnlab.v3mobileplus
                         - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                         > 외환카드 안심클릭
                         - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                         - package name : com.TouchEn.mVaccine.webs
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 씨티카드 안심클릭
                         - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                         - package name : com.TouchEn.mVaccine.webs
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 하나SK카드 안심클릭
                         - 백신앱 : V3 Mobile Plus 2.0
                         - package name : com.ahnlab.v3mobileplus
                         - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                         > 하나카드 안심클릭
                         - 백신앱 : V3 Mobile Plus 2.0
                         - package name : com.ahnlab.v3mobileplus
                         - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                         > 롯데카드
                         - 백신이 설치되어 있지 않아도, 결제페이지로 이동

                         */

                        /*************************************************************************************/
                        Log.d("<INIPAYMOBILE>", "Custom URL (intent://) 로 인입될시 마켓으로 이동되는 예외 처리: " );
                        /*************************************************************************************/

                        try {

                            Intent excepIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                            String packageNm = excepIntent.getPackage();

                            Log.d("<INIPAYMOBILE>", "excepIntent getPackage : " + packageNm );

                            excepIntent = new Intent(Intent.ACTION_VIEW);
                            excepIntent.setData(Uri.parse("market://search?q="+packageNm));

                            startActivity(excepIntent);

                        } catch (URISyntaxException e1) {
                            Log.e("<INIPAYMOBILE>", "INTENT:// 인입될시 예외 처리  오류 : " + e1 );
                        }

                    }
                }

            }
            else
            {
                view.loadUrl(url);
                return false;
            }

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (bHistoryClear) {
                bHistoryClear = false;
                webView.clearHistory();
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            view.loadData("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>" +
                    "</head><body>" + "요청실패 : (" + error.getErrorCode() + ")" + error.getDescription() + "</body></html>", "text/html", "utf-8");
        }
    }

    class CommonChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

    }
}
