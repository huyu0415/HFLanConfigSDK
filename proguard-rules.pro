######################################################################
#
#   1.语法规则
#       （成员变量不管是否被引用，都不会删除，若指定就保留，未指定就重命名）
#       加 names 和 不加 names 区别：
#               不加 names:   当 类的成员没有被引用时，这个类不会被删除
#               加   names:   当 类的成员没有被引用时，这个类会被删除
#
#       关键字                         1.压缩删除              2.类名是否保留（删除后）              3.成员是否保留（删除后）                   描述
#
#   keep                         先删除 未被引用的方法                  保留                   再保留 指定成员名，最后重命名 其余成员名
#
#   keepclassmembers             先删除 未被引用的方法                  重名                   再保留 指定成员名，最后重命名 其余成员名
#
#   keepclasseswithmembers       先删除 未被引用的方法       指定成员存在就保留，不存在就重名     再保留 指定成员名，最后重命名 其余成员名
#
#   keepnames                    先删除 未被引用的方法                  保留                   再保留 指定成员名，最后重命名 其余成员名
#
#   keepclassmembernames         先删除 未被引用的方法                  重名                   再保留 指定成员名，最后重命名 其余成员名
#
#   keepclasseswithmembernames   先删除 未被引用的方法       指定成员存在就保留，不存在就重名     再保留 指定成员名，最后重命名 其余成员名
#
#   -keepattributes attribute_name,...                  保护给定的可选属性，例如LocalVariableTable, Synthetic 等.
#   -dontwarn [class_filter]      不打印指定类的警告信息（不是每个第三方SDK都需要-dontwarn，这取决于混淆时第三方SDK是否出现警告，需要的时候再加上）
#
#
#       通配符	            描述
#       <field>	            匹配类中的所有字段
#       <method>	        匹配类中的所有方法
#       <init>	            匹配类中的所有构造函数
#       ?	                匹配单个字符
#       %	                匹配任何基础类型的类型名
#       *	                匹配任意长度字符，但不含包名分隔符(.)。
#                               比如说我们的完整类名是com.example.test.MyActivity，使用com.*，或者com.exmaple.*都是无法匹配的，因为*无法匹配包名中的分隔符，
#                               正确的匹配方式是com.exmaple.*.*，或者com.exmaple.test.*，这些都是可以的。
#                               但如果你不写任何其它内容，只有一个*，那就表示匹配所有的东西。
#       *(当用在类里面时)	    匹配任何字段和方法
#       **	                匹配任意长度字符，并且包含包名分隔符(.)。
#                               比如 android.support.** 就可以匹配android.support包下的所有内容，包括任意长度的子包。
#       ***	                匹配任意参数类型。
#                               比如void set*(***)就能匹配任意传入的参数类型，*** get*()就能匹配任意返回值的类型。
#       …	                匹配任意长度的任意类型参数。
#                               比如void test(…)就能匹配任意void test(String a)或者是void test(int a, String b)这些方法。
#       $	                指内部类
#
#
#
#   2.不能混淆的代码
#       1.需要反射的代码
#       2.系统接口（AndroidMainfest 中的类（四大组件，Application 的子类）， Framework 层下所有的类，support包， webview，js等）
#       3.Jni接口
#       4.需要序列号和反序列化的代码
#       5.与服务端进行元数据交互的实体类（JSON、XML中对应的实体类）
#       6.GSON、fastjson 等框架时，所写的 JSON 实体类
#       7.第三方库一般是不需要混淆的
#
######################################################################




######################################################################0
#
# 1.特殊处理
#

# 实体类（在开发的时候我们可以将所有的实体类放在一个包内，这样我们写一次混淆就行了）
-keep public class com.huyu.lanconfig_hf.entity.** {
    public void set*(...);
    public *** get*();
    public *** is*();
}
-keep public class com.huyu.lanconfig_hf.model.** {*;}
-keep public class com.huyu.lanconfig_hf.net.INetworkTransmission {*;}

# 接口（对外接口的public类名和成员名，否则外部无法调用）
-keep public interface com.huyu.lanconfig_hf.listener.**{*;}


# 内部类或内部接口
#-keep class com.secrui.s72.listener.ScreenListener$* {*;}
#-keep class com.secrui.s72.but.CircularSeekBar$* {*;}



# 项目中其他Module的警告
#-dontwarn com.test.**

# 反射类
# 一般而言，使用反射一般会有以下方式，可以搜索代码，找到相关的类，然后在混淆配置里面进行保留
# Class.forName(“SomeClass”)
# SomeClass.class
# SomeClass.class.getField(“someField”)
# SomeClass.class.getDeclaredField(“someField”)
# SomeClass.class.getMethod(“someMethod”, new Class[] {})
# SomeClass.class.getMethod(“someMethod”, new Class[] { A.class })
# SomeClass.class.getMethod(“someMethod”, new Class[] { A.class, B.class })
# SomeClass.class.getDeclaredMethod(“someMethod”, new Class[] {})
# SomeClass.class.getDeclaredMethod(“someMethod”, new Class[] { A.class })
# SomeClass.class.getDeclaredMethod(“someMethod”, new Class[] { A.class, B.class })
# AtomicIntegerFieldUpdater.newUpdater(SomeClass.class, “someField”)
# AtomicLongFieldUpdater.newUpdater(SomeClass.class, “someField”)
# AtomicReferenceFieldUpdater.newUpdater(SomeClass.class, SomeType.class, “someField”)
#
#-keep class com.huyu.reflectClass { *; }  # 保留反射的 类名 和 类的所有成员


# js交互
# 在app中与HTML5的JavaScript的交互进行特殊处理，如
# package com.ljd.example;
#
# public class JSInterface {
#     @JavascriptInterface
#     public void callAndroidMethod(){
#         // do something
#     }
# }
# 我们需要确保这些js要调用的原生方法不能够被混淆，于是我们需要做如下处理
# -keepclassmembers class com.ljd.example.JSInterface {
#    <methods>;
# }

#
#
#
######################################################################



######################################################################
#
# 2.常用第三方jar处理
# (格式)
# -dontwarn com.alibaba.**
# -keep class com.alibaba.** {*;}
#

# 机智云
-dontwarn com.gizwits.**
-keep class com.gizwits.**{ *;}

# async-http
-keep class com.loopj.android.http.** {*;}
-dontwarn com.google.zxing.**

#不混淆 android 6.0 以后的 HttpClient 问题
-dontwarn android.net.compatibility.**
-dontwarn android.net.http.**
-dontwarn com.android.internal.http.multipart.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**
-keep class android.net.compatibility.**{*;}
-keep class android.net.http.**{*;}
-keep class com.android.internal.http.multipart.**{*;}
-keep class org.apache.commons.**{*;}
-keep class org.apache.http.**{*;}

# zxing
-keep class com.google.zxing.** {*;}
-dontwarn com.google.zxing.**

# Volley
-keep class com.android.volley.** {*;}
-keep class com.android.volley.toolbox.** {*;}
-keep class com.android.volley.Response$* { *; }
-keep class com.android.volley.Request$* { *; }
-keep class com.android.volley.RequestQueue$* { *; }
-keep class com.android.volley.toolbox.HurlStack$* { *; }
-keep class com.android.volley.toolbox.ImageLoader$* { *; }


#nineoldandroids
-dontwarn com.nineoldandroids.*
-keep class com.nineoldandroids.** { *;}


# 阿里产品（fastjson等）
-dontwarn com.alibaba.**
-keep class com.alibaba.** { *; }

# 腾讯产品（QQ授权，微信授权等）
-dontwarn com.tencent.**
-keep class com.tencent.** {*;}

# 百度产品（推送，自升级等）
-dontwarn com.baidu.**
-keep class com.baidu.**{*; }

# 极光推送
-dontwarn cn.jpush.**
-dontwarn cn.jiguang.**
-keep class cn.jpush.** { *; }
-keep class cn.jiguang.** { *; }

# 小米推送
#可以防止一个误报的 warning 导致无法成功编译，如果编译使用的 Android 版本是 23。
-dontwarn com.xiaomi.push.**
-keep class com.xiaomi.** {*;}
#这里 com.xiaomi.mipushdemo.DemoMessageRreceiver 改成 app 中定义的完整类名
#-keep class cn.jpush.android.service.PluginXiaomiPlatformsReceiver {*;}

# 魅族push
-keep class com.meizu.cloud.pushsdk.** { *; }
-dontwarn  com.meizu.cloud.pushsdk.**
-keep class com.meizu.nebula.** { *; }
-dontwarn com.meizu.nebula.**
-keep class com.meizu.push.** { *; }
-dontwarn com.meizu.push.**

# 华为push
-dontwarn  com.huawei.**
-keep class com.huawei.** { *; }
# hmscore-support: remote transport
-keep class * extends com.huawei.hms.core.aidl.IMessageEntity { *; }
# hmscore-support: remote transport
-keepclasseswithmembers class * implements com.huawei.hms.support.api.transport.DatagramTransport {
 <init>(...);
}
# manifest: provider for updates
-keep public class com.huawei.hms.update.provider.UpdateProvider { public *; protected *; }

#阿里push
-keepclasseswithmembernames class ** {
    native <methods>;
}
-keep class sun.misc.Unsafe { *; }
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-keep class com.ut.** {*;}
-keep class com.ta.** {*;}
-keep class anet.**{*;}
-keep class anetwork.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-keep class android.os.**{*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**
-dontwarn anetwork.**
-dontwarn com.ut.**
-dontwarn com.ta.**

# GCM/FCM通道
-keep class com.google.firebase.**{*;}
-dontwarn com.google.firebase.**

# 讯飞语音
-dontwarn com.iflytek.**
-keep class com.iflytek.** {*;}

#ImmersionBar
-keep class com.gyf.barlibrary.* {*;}

#Hellocharts
-keep class lecho.lib.hellocharts.* {*;}

#bcprov-jdk15on-154
-dontwarn org.bouncycastle.**
-keep class org.bouncycastle.** {*;}

# 技威--开始
-keep public class com.p2p.core.MediaPlayer{
	public private <methods>;
	private int mNativeContext;
}
-keep public class com.p2p.core.VideoView{
	public private <methods>;
}
-keep public class com.cloud.core.MediaPlayer{
	public private <methods>;
	private int mNativeContext;
}
-keep public class com.cloud.core.VideoView{
	public private <methods>;
}
-keep class com.juan.** {*;}
-keep class com.larksmart.emtmf.jni.* {*;}
-keep class com.lsemtmf.genersdk.tools.** {*;}
#emailUtils
-dontwarn com.sun.**
-dontwarn javax.mail.**
-dontwarn javax.activation.**
-dontwarn myjava.awt.datatransfer.**
-dontwarn com.wxy.libemail.email.**
-keep class com.sun.** {*;}
-keep class javax.mail.** {*;}
-keep class javax.activation.* {*;}
-keep class com.wxy.libemail.email.* {*;}
-keep class myjava.awt.datatransfer.* {*;}
#javabase64
-keep class it.sauronsoftware.base64.* {*;}
#pinyin4j
-dontwarn net.soureceforge.pinyin4j.**
-dontwarn demo.**
-keep class net.sourceforge.pinyin4j.** { *;}
-keep class demo.** { *;}
-keep class com.hp.** { *;}
# UIL
-keep class com.nostra13.universalimageloader.** { *; }
-keepclassmembers class com.nostra13.universalimageloader.** {*;}
# 技威--结束

#雄迈
-keep class com.xm.** {*;}
-keep class com.basic.** {*;}
-keep class com.libra.sinvoice.** {*;}

#萤石云
-dontwarn com.ezviz.push.sdk.**
-keep class com.ezviz.push.sdk.** { *;}
-dontwarn com.githang.android.apnbb.**
-keep class com.githang.android.apnbb.** { *;}
-dontwarn com.hik.**
-keep class com.hik.** { *;}
-dontwarn com.hikvision.**
-keep class com.hikvision.** { *;}
-dontwarn com.videogo.**
-keep class com.videogo.** { *;}
-dontwarn org.androidpn.client.**
-keep class org.androidpn.client.** { *;}
-keep class org.MediaPlayer.PlayM4.** { *;}
-keep class org.eclipse.paho.client.mqttv3.** { *;}

#Gson混淆配置
-keepattributes Annotation
-keep class sun.misc.Unsafe { *; }
-keep class com.idea.fifaalarmclock.entity.*
-keep class com.google.gson.stream.* { *; }



#
#
#############################################################################



######################################################################
#
# 3.基础不用动的规则
#
-dontskipnonpubliclibraryclasses # 不忽略非公共的库类
-dontskipnonpubliclibraryclassmembers   # 指定不去忽略非公共库的类成员
-optimizationpasses 5            # 指定代码的压缩级别在0~7之间，默认为5，一般不做修改
-dontusemixedcaseclassnames      # 不使用大小写混合，混合后的类名为小写
-dontpreverify                   # 混淆时不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度
-verbose                         # 混淆时记录日志
-dontoptimize                    # 不优化输入的类文件，优化选项是默认打开的（如果需要删除log,此选项需要去除）
-keepattributes Signature        # 不混淆泛型，如果混淆报错建议关掉
-keepattributes EnclosingMethod  # 不混淆反射
-keepattributes *Annotation*,InnerClasses     # 保持注解，内部类
-keepattributes SourceFile,LineNumberTable    # 抛出异常时保留代码行号
-keepattributes Deprecated,Exceptions    #
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法（谷歌推荐）

# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆,因为这些子类都有可能被外部调用
-keep public class * extends android.app.Application
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.vending.licensing.ILicensingService
-keep public class * extends android.os.IInterface

#记录生成的日志数据
-dump class_files.txt            #列出 apk包内所有class的内部结构
-printseeds seeds.txt            #列出 未混淆的类和成员
-printusage unused.txt           #列出 未被使用的代码
-printmapping mapping.txt        #列出 混淆前后的映射

-dontwarn android.support.**                        # 不提示兼容库的错误警告
-keep class android.support.** {*;}                 # 保留support下的所有类及其内部类
-keep public class * extends android.support.**     # 保留support下v4,v7等子类
-keep class **.R$* {*;}          # 保留R下面的资源

#保持 含有 native 函数 的 类 和 native 成员函数 不被混淆
-keepclasseswithmembernames class * {
   native <methods>;
}

#保持Activity中的方法参数是view的方法,如 xml 布局使用了 doClick 等事件
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保留枚举类不被混淆（如果报错，可以去掉这项）
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep public class * extends android.view.View {
   *** get*();
   void set*(***);
   public <init>(android.content.Context);
   public <init>(android.content.Context, android.util.AttributeSet);
   public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

#-----------处理webView处理---------------
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}

#
#
#
######################################################################

