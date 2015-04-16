SwipingContainer
=================================== 
Android容器，基于FrameLayout实现
-----------------------------------
    可添加多个子视图，一次只显示一个子视图。通过水平拖拽或快速滑动切换显示的子视图。
###图例
    ![github](https://github.com/apoliszk/SwipingContainer/demo.gif "github")
###可在layout xml中使用
    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent">
    
        <com.apolis.swipingcontainer.SwipingContainer
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
        </com.apolis.swipingcontainer.SwipingContainer>
    </RelativeLayout>

###获取当前显示子项的index
    int visibleIndex = swipingContainer.getVisibleIndex();
###让容器滚动到第n个子项
    swipingContainer.swipeToIndex(n);
