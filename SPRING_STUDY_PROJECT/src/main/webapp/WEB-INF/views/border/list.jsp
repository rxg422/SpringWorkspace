<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
.list-group-item + .list-group-item {
    margin-top: 10px;
}
</style>
</head>
<body>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="container my-4">
    <section>
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="h5">게시물 목록</h2>
            <a href="insert" class="btn btn-primary">글쓰기</a>
        </div>
        <div class="list-group-row flex-wrap row">
           <article class="col-md-4">
                <div class="card mb-4">
                    <img src="/study/resources/upload/study/book1.jpg" class="card-img-top thumbnail" alt="Image 1">
                    <div class="card-body">
                        <h5 class="card-title"><a class="text-dark" href="detail/2">안보는 책 팝니다</a></h5>
                        <p class="card-text">거의 새 상품입니다 ㅎ</p>
                        <small class="text-muted">작성자: KH 수달 | 작성일: 2024-06-18</small>
                    </div>
                </div>
            </article>
            <!-- 게시물 2 -->
            <article class="col-md-4">
                <div class="card mb-4">
                    <img src="/study/resources/upload/study/2.png" class="card-img-top thumbnail" alt="Image 2">
                    <div class="card-body">
                        <h5 class="card-title"><a class="text-dark" href="detail/1">스터디 같이 하실 분</a></h5>
                        <p class="card-text">모집중(3/5)</p>
                        <small class="text-muted">작성자: KH 수달 | 작성일: 2024-06-17</small>
                    </div>
                </div>
            </article>   
            <article class="col-md-4">
                <div class="card mb-4">
                    <img src="/study/resources/upload/study/book1.jpg" class="card-img-top thumbnail" alt="Image 1">
                    <div class="card-body">
                        <h5 class="card-title"><a class="text-dark" href="detail/2">안보는 책 팝니다</a></h5>
                        <p class="card-text">거의 새 상품입니다 ㅎ</p>
                        <small class="text-muted">작성자: KH 수달 | 작성일: 2024-06-18</small>
                    </div>
                </div>
            </article>
            <!-- 게시물 2 -->
            <article class="col-md-4">
                <div class="card mb-4">
                    <img src="/study/resources/upload/study/2.png" class="card-img-top thumbnail" alt="Image 2">
                    <div class="card-body">
                        <h5 class="card-title"><a class="text-dark" href="detail/1">스터디 같이 하실 분</a></h5>
                        <p class="card-text">모집중(3/5)</p>
                        <small class="text-muted">작성자: KH 수달 | 작성일: 2024-06-17</small>
                    </div>
                </div>
            </article> 
            <article class="col-md-4">
                <div class="card mb-4">
                    <img src="/study/resources/upload/study/book1.jpg" class="card-img-top thumbnail" alt="Image 1">
                    <div class="card-body">
                        <h5 class="card-title"><a class="text-dark" href="detail/2">안보는 책 팝니다</a></h5>
                        <p class="card-text">거의 새 상품입니다 ㅎ</p>
                        <small class="text-muted">작성자: KH 수달 | 작성일: 2024-06-18</small>
                    </div>
                </div>
            </article>
            <!-- 게시물 2 -->
            <article class="col-md-4">
                <div class="card mb-4">
                    <img src="/study/resources/upload/study/2.png" class="card-img-top thumbnail" alt="Image 2">
                    <div class="card-body">
                        <h5 class="card-title"><a class="text-dark" href="detail/1">스터디 같이 하실 분</a></h5>
                        <p class="card-text">모집중(3/5)</p>
                        <small class="text-muted">작성자: KH 수달 | 작성일: 2024-06-17</small>
                    </div>
                </div>
            </article>     
        </div>
    </section>
</main>


<jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
