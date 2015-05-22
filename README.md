#介绍
可以无限循环，自动旋转，停靠的3D旋转布局控件,无需编写代码，直接在布局中加入自己的布局即可。<br />
仅使用三角函数,使用ValueAnimation,继承FrameLayout.兼容所有滑动组件
##优势
*3d旋转GrallyView，继承FrameLayout<br />
*支持自动旋转<br />
*可直接在xml添加元素即可添加列数据。无需编写代码添加<br />
*优良的兼容性，和尺寸控制<br />
##如何使用？
<code>
<com.cry.animation.LoopView<br />
        android:id="@+id/loopview"<br />
        android:layout_width="fill_parent"<br />
        android:layout_height="fill_parent"<br />
        android:gravity="center_vertical"<br />
        ><br />
  <!--  此处添加你的布局元素，可以用layout包裹 --!><br />
       <ImageView<br />
         android:layout_width="130dp"<br />
         android:layout_height="20dp"<br />
         android:scaleType="fitXY"<br />
         android:paddingLeft="20dp"<br />
         android:paddingRight="20dp"<br />
         android:src="@drawable/image_shader"/>
       <br />...............<br />
 </com.cry.animation.LoopView>
 <code>
##问题反馈和联系方式
qq:347284221<br />
邮箱:zhuxiujia@qq.com<br />
![ABC](device-2015-05-22-100146.png)