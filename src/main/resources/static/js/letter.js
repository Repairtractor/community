$(function () {
    $("#sendBtn").click(send_letter);
    $(".close").click(delete_msg);
});

function send_letter() {
    $("#sendModal").modal("hide");

    let toName = $("#recipient-name").val();
    let content = $("#message-text").val();
    $.post(
        CONTEXT_PATH + "/addMessage",
        {"toName": toName, "content": content},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#hintModal").text("发送成功");
            } else {
                $("#hintModal").text(data.msg);
            }

            $("#hintModal").modal("show");

            setTimeout(function () {
                $("#hintModal").modal("hide");
                location.reload();
            }, 2000);
        }
    )



}

function delete_msg() {
    // TODO 删除数据
    $(this).parents(".media").remove();
}