# Sample app demonstrating integration of Xsilver Crypto checkout 

<h3>This Android project showcases the steps to consume the Xsilver Crypto checkout.<br>It is made up of 2 modules -</h3>

<h4>1. xsilver-sample</h4>
- Android library module with basic functionality<br>
- Publishes the generated library file onto the GitHub Package Registry<br>
- The build.gradle file inside this module has the code (plugin, tasks and authentication) related to publishing the library<br>

<h4>2. app</h4>
- Sample Android application module with the build.gradle file that shows the code for consuming an Xsilver Crypto Android library from GitHub.
<br>
<h2>Integration</h2>
There are 2 steps to SDK integration 
<br>1. Register with XSilver as a partner</b>
<br>2. Follow the Integration steps below for calling SDK from your application
<br><br><b>Registration with XSilver</b>
<br>To generate your partner referral code, do following 
<br>1. Visit https://partner.xsilver.com and sign yourself up.
<br>2. In partner settings tab, submit a unique username.
<br>3. You will be provided with a partner referral URL like "https://xsilver.com/refer/unique_user_name"
      <br>4. Use this referral URL while calling the SDK primary class as shown in <a href="https://github.com/Nexxo-Xsilver/xsilver-sample/blob/dev_shadab/app/src/main/java/com/aditya/galileoSdk/MainActivity.kt" target="_blank">MainActivity</a>.
      <br>
      <br><b>Integration Steps</b><br>
1. Add this to settings.gradle file of your project inside repositories:<br>
<i>maven { url "https://jitpack.io" }</i>
<br>
2. Add this line to your app's build.gradle inside the dependencies section: <br>
<i>dependencies {
      implementation 'com.github.Nexxo-Xsilver:xsilver-sample:1.1.5'
}</i>
<br>
3. You can check the latest build version on <a href="https://jitpack.io/#Nexxo-Xsilver/xsilver-sample" target="_blank">Jitpack</a>
<br>
4. To hand over control to SDK for buying cryptocurrncy, refer <a href="https://github.com/Nexxo-Xsilver/xsilver-sample/blob/dev_shadab/app/src/main/java/com/aditya/galileoSdk/MainActivity.kt" target="_blank">MainActivity</a>.
<br>
5. For permissions required, refer <a href="https://github.com/Nexxo-Xsilver/xsilver-sample/blob/dev_shadab/app/src/main/AndroidManifest.xml)" target="_blank">Android Manifest</a>.<br>
6. For proguard rules when you are releasing the app, refer <a href="https://github.com/Nexxo-Xsilver/xsilver-sample/blob/dev_shadab/app/proguard-rules.pro)" target="_blank">proguard-project.txt</a>.
<br><br>
## 📈 GitHub Stats 

[![Nexxo Xsilver's github stats](https://github-readme-stats.vercel.app/api?username=Nexxo-Xsilver)](https://github.com/Nexxo-Xsilver)

[![Top Langs](https://github-readme-stats.vercel.app/api/top-langs/?username=Nexxo-Xsilver&layout=compact)](https://github.com/Nexxo-Xsilver)

[![Visitors](https://visitor-badge.glitch.me/badge?page_id=Nexxo-Xsilver.Nexxo-Xsilver)](https://github.com/Nexxo-Xsilver)
