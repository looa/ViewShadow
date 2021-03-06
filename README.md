# ViewShadow

视图阴影效果加载工具（低版本兼容 Android API 15 And Above.）

## Usage

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```groovy
dependencies {
	compile 'com.github.looa:ViewShadow:1.1.3'
}
```

## Intro
ViewShadow是能为Android应用中任意视图加载阴影效果的工具类。
<br>
ViewShadow is a simple class you can use in your Android application
to load shadows for your view.
<br>
<br>
注意，该工具只支持渲染阴影，并不支持高度设定。
 
Sample
-----
 
 ![screenshots](./screenshot/device-2017-07-28-145236.png)

Quick Start
-----
 

```java
public class MyActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content view, etc.
        ViewShadow.setElevation(view, elevation);
        ViewShadow.setElevation(view, elevation, color);
    }
}
 ```


Please refer to the documentation in ViewShadow.java for more info.

## TODO
阴影支持跟随View缩放等动画效果。
