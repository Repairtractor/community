$(function (){
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});


function like(btn,entityType,entityId,targetId,postId) {
    $.post(
        CONTEXT_PATH+"/like",
        {"entityType":entityType,"entityId":entityId,"targetId":targetId,"postId":postId},
        function (data){
            data=$.parseJSON(data);
            if (data.code==0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':'赞');
            }else {
                alert(data.msg);
            }
        }
    )
}

function setTop(){
    $.post(
        CONTEXT_PATH+"/top",
        {
            "id":$("#postId").val()
        },
        function (data){
            data=$.parseJSON(data);
            if (data.code==0){
                $("#topBtn").attr("disabled","disabled");
            }else {
                alert(data.msg);
            }
        }
    )
}
function setWonderful(){

    $.post(
        CONTEXT_PATH+"/wonderful",
        {
            "id":$("#postId").val()
        },
        function (data){
            data=$.parseJSON(data);
            if (data.code==0){
                $("#wonderfulBtn").attr("disabled","disabled");
            }else {
                alert(data.msg);
            }
        }
    )
}
function setDelete(){

    $.post(
        CONTEXT_PATH+"/delete",
        {
            "id":$("#postId").val()
        },
        function (data){
            data=$.parseJSON(data);
            if (data.code==0){
                location.href=CONTEXT_PATH+"/index";
            }else {
                alert(data.msg);
            }
        }
    )
}
