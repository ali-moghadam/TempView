## TempViewDemo   &nbsp; [![APK](https://img.shields.io/badge/APK-Demo-brightgreen.svg)](https://github.com/alipapital/TempViewDemo/blob/master/app/tempView%20v1.apk)

show temp in smart home app

<br />

download and use sample app [demo.apk](https://github.com/alipapital/TempViewDemo/raw/master/app/tempView%20v1.apk)

<br />

#### Step 1. Add the JitPack repository to your build file
```gradle	
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

<br />

#### Step 2. Add the dependency
```gradle	
	dependencies {
	        implementation 'com.github.alipapital:TempView:0.2'
	}
```

<br />

#### Step 3. Add TempView in your layout

<br />

### Flexible Mode
<img src="https://github.com/alipapital/TempView/blob/master/files/flexible.gif" width="250" height="250" />


```xml
    <com.alirnp.tempview.TempView
        android:id="@+id/tempView1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:tv_color_background_progress="#B0BEC5"
        app:tv_color_value="#1a8dff"
        app:tv_color_degree="#1a8dff"
        app:tv_color_progress="#1a8dff"
        app:tv_color_text="#1a8dff"
        app:tv_current_value="6"
        app:tv_is_indicator="false"
        app:tv_max_value="14"
        app:tv_min_value="-2"
        app:tv_stroke_width_background_progress="25dp" />
```
<br />

### Indicator Mode
<img src="https://raw.githubusercontent.com/alipapital/TempView/master/files/indicator.jpg" width="250" height="250" />

```xml 
    <com.alirnp.tempview.TempView
        android:id="@+id/tempView2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:tv_color_background_progress="#B0BEC5"
        app:tv_color_degree="#673AB7"
        app:tv_color_progress="#673AB7"
        app:tv_color_text="#673AB7"
        app:tv_current_value="6"
        app:tv_is_indicator="true"
        app:tv_max_value="14"
        app:tv_min_value="-2"
        app:tv_stroke_width_background_progress="25dp"
        app:tv_text_center_size="30sp"
        app:tv_text_status="Freezer Temp"
        app:tv_text_top_size="16sp" />

``` 
<br/>

### Properties

|xml|java|Type|Description|
|:---:|:---:|:---:|:---:|
|`tv_current_value`|`setCurrentValue`|float|set current temp|
|`tv_min_value`|`setMinValue`|float|set minimum temp|
|`tv_max_value`|`setMaxValue`|float|set maximun temp|

<br/> 

### Programmatically
```java
   TempView mTempView = findViewById(R.id.tempView);
   
   mTempView.setOnSeekCirclesListener(new TempView.OnSeekChangeListener() {
            @Override
            public void onSeekChange(int value) {
                textView.setText(String.format("value = %s", value));
            }

            @Override
            public void onSeekComplete(int value) {
                textView.setText(String.format("value = %s", value));
            }
        });
