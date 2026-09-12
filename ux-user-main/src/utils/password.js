export const  checkPassword = (password)=>{
    const minLength = 8;
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+{}\[\]:;"'<>,.?/-]).{8,}$/;
  
    if (!password) {
      return false;
    }
  
    if (!regex.test(password)) {
      return false;
    }
   if(password.length < minLength){
    return false;
   }
    return true;
  }
