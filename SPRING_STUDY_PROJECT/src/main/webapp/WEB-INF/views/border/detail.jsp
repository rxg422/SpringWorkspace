<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>


 <main class="container my-4">
     <section>
         <h2 class="h5">안보는 책 팝니다</h2>
         <p class="text-muted">작성자: KH 수달 | 작성일: 2024-06-18</p>
         <div>
             <p>거의 새 상품입니다 ㅎ</p>
             <div class="list-group-row flex-wrap row">
	             <article class="col-md-4">
	                <div class="card mb-4">
	                    <img src="/study/resources/upload/study/book1.jpg" class="card-img-top thumbnail" alt="Image 1">
	                </div>
	             </article>
	             <article class="col-md-4">
	                <div class="card mb-4">
	                    <img src="/study/resources/upload/study/book2.jpg" class="card-img-top thumbnail" alt="Image 1">
	                </div>
	             </article>
	             <article class="col-md-4">
	                <div class="card mb-4">
	                    <img src="/study/resources/upload/study/book3.PNG" class="card-img-top thumbnail" alt="Image 1">
	                </div>
	             </article>
             </div>
             <p>JAVA, HTML, JSP, REACT 다 있습니다. 가격은 선제시요</p>
         </div>
         <a href="../list" class="btn btn-secondary mt-3">목록으로</a>
     </section>
 </main>


<jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
