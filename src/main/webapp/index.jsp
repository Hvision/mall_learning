<html>

<body>
<%@page pageEncoding="UTF-8"%>
<h2>Hello World!</h2>
spring mvc上传文件测试
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" name="spring mvc上传文件"/>
</form>

富文本上传文件测试
<form name="form2" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" name="富文本上传"/>
</form>

<form name="form3" action="/user/login.do" method="post">
    <input type="text" name="username" id="username" value="admin">
    <input type="text" name="password" id="password" value="admin">
    <input type="submit" name="登录"/>
</form>
</body>
</html>
