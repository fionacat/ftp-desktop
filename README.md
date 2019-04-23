# ftp-desktop
java实现模仿filezilla的ftp客户端（大二上java期末大作业）
## 初始界面
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/%E5%88%9D%E5%A7%8B%E7%95%8C%E9%9D%A2.png)
## 登录界面（输入主机地址，用户名，密码，端口，点击登录后）
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/%E7%99%BB%E9%99%86%E7%95%8C%E9%9D%A2.png)

## 如果连接失败，弹出提示对话框
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/3.png)
## 连接成功后，如果点击断开，则对话框有提示（此时可重新点击连接）
 
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/4.png)






## 连接之后，如果再次点击连接，弹出窗口
 
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/5.png)
## 点击窗口的关闭按钮，弹出提示对话框确认是否关闭
 
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/6.png)







## 选择任意本地路径的树节点，会展开树节点下的所有文件和目录，并生成本地文件列表
 
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/7.png)



## 点击任意远程服务器下的任意树节点，会展开该目录下的所有文件和目录，并生成远程文件列表
 
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/8.png)
## 右键本地电脑的任意树节点，弹出菜单项
上传：直接下载该文件（如果不是文件则弹出提示对话框）
添加到队列：将该文件的信息添加到队列中，等待上传
进入该目录：展开该树节点并列出该目录下的所有文件和目录列表
 
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/9.png)




## 右键本地列表，弹出菜单项
上传：直接下载该文件（如果不是文件则弹出提示对话框）
添加到队列：将该文件的信息添加到队列中，等待上传
进入该目录：列出该目录下的所有文件和目录列表

![](https://github.com/fionacat/ftp-desktop/blob/master/bin/10.png) 
## 右键远程文件列表下的任意文件，弹出菜单项
下载：直接下载该文件（如果文件不符合或本地路径错误，会弹出提示）
添加到队列：将该项添加到队列准备下载（如果文件不符合或本地路径错误，会弹出提示）
进入目录：如果是目录，则进入该目录，展开该目录的树节点并展示该目录下的所有文件和目录列表

![](https://github.com/fionacat/ftp-desktop/blob/master/bin/11.png) 
## 右键点击远程文件选择“下载”或“添加队列”，会弹出提示对话框“请选择一个可添加文件的本地目录”
（原因在于只选择了远程文件，却没有选择要将文件下载至哪个本地目录）

![](https://github.com/fionacat/ftp-desktop/blob/master/bin/12.png) 
## 右键点击远程目录选择“下载”或“添加队列”，会弹出提示对话框“请选择一个文件”
（原因在于选择的是目录不是文件）

![](https://github.com/fionacat/ftp-desktop/blob/master/bin/13.png) 
## 下载单个文件过程
（将该文件下载到E盘）

![](https://github.com/fionacat/ftp-desktop/blob/master/bin/14.png) 
如果下载成功
传输成功的标签页会出现下载成功的文件信息，
对话框提示传输成功
重新点击E树节点，会发现目录中有刚刚下载的文件
 


## 可选择任意远程文件和任意本地目录，添加到队列中
（表示准备将任意远程文件下载到任意本地目录下）
 
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/15.png)




右键队列点击“处理队列”，会开始依次下载文件
 
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/16.png)

下载成功
 

![](https://github.com/fionacat/ftp-desktop/blob/master/bin/17.png)




## 右键传输成功标签页的任意项，弹出菜单项
移除选定：将选定的一项移除传输成功标签页
移除所有：将所有项都移出传输成功标签页
重置并将选定文件加入队列：清空传输成功标签页并将选定项加入队列
重置并将选定文件加入队列：清空传输成功标签页并将所有项加入队列

![](https://github.com/fionacat/ftp-desktop/blob/master/bin/18.png) 

## 右键队列标签页的任意项，弹出菜单项
处理队列：对队列标签页中的文件进行处理，将远程文件下载到对应目录，或将本地文件上传到服务器
移除选定文件：将选定的文件移出队列
移除所有：将所有文件都移出
 
![](https://github.com/fionacat/ftp-desktop/blob/master/bin/19.png)
