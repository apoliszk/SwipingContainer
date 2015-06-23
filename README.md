SwipingContainer
=================================== 
Android容器，基于FrameLayout实现
-----------------------------------
    可添加多个子视图，一次只显示一个子视图。通过水平拖拽或快速滑动切换显示的子视图。
###图例
![image](https://raw.githubusercontent.com/apoliszk/SwipingContainer/master/demo.gif)
###在layout xml中使用
    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent">
    
        <com.apolis.android.swipingcontainer.SwipingContainer
            android:id="@+id/swipingContainer"
            android:layout_width="match_parent"
            android:layout_height="match_parent">
    
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="#433166"
                android:gravity="center"
                android:orientation="vertical">
    
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="First View"
                    android:textColor="@android:color/white"/>
    
                <Button
                    android:id="@+id/btnFirst"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:onClick="btnClickHandler"
                    android:text="Go to second View"/>
            </LinearLayout>
    
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="#626626"
                android:gravity="center"
                android:orientation="vertical">
    
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Second View"
                    android:textColor="@android:color/white"/>
    
                <Button
                    android:id="@+id/btnSecond"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:onClick="btnClickHandler"
                    android:text="Go to first View"/>
            </LinearLayout>
        </com.apolis.android.swipingcontainer.SwipingContainer>
    </RelativeLayout>
###设置支持循环
    swipingContainer.setCyclic(true);
###获取当前显示子视图的index
    int visibleIndex = swipingContainer.getVisibleIndex();
###让容器显示第n个子视图
    swipingContainer.swipeToIndex(n, true); // 带动画效果
    swipingContainer.swipeToIndex(n, false); // 不带动画效果
###监听显示子视图改变事件
    swipingContainer.setVisibleIndexChangeListener(
        new SwipingContainer.VisibleIndexChangeListener() {
            @Override
            public void onVisibleIndexChanging(float index) {
                // 划动过程中，该方法会被调用，index是滑动进度
                // 例如index = 0.1，代表在第一个子视图与第二个子视图之间，第二个子视图显示了10%
            }
        
            @Override
            public void onVisibleIndexChange(int index) {
                // 滑动结束后，该方法会被调用，index是当前显示的子视图index
            }
        });
