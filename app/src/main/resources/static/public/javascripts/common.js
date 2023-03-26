function gRecaptchaCallback(response){
  document.getElementById("gRecaptchaResponse").value = response;
  document.getElementsByName("subscription.submit")[0].disabled = false;
}

function submitSubscriptionForm(){
  document.getElementsByName("timeZone")[0].value=Intl.DateTimeFormat().resolvedOptions().timeZone;
  document.getElementById("subscribe")[0].submit();
}

