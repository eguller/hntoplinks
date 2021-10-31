function submitSubscriptionForm(){
        var d = new Date()
        var n = d.getTimezoneOffset();
        var elem = document.getElementsByName("timeZoneOffSet")[0];
        elem.value = n;
        subscribeForm = document.getElementById("subscribe");
        subscribeForm.submit();
    }