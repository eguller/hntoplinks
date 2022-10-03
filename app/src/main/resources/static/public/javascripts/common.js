function enableSubscriptionFormSubmit(){
  document.getElementsByName("subscription.submit")[0].disabled = false;
}

function submitSubscriptionForm(){
  document.getElementsByName("subscription.timeZone")[0].value=Intl.DateTimeFormat().resolvedOptions().timeZone;
  document.getElementById("subscribe")[0].submit();
}

