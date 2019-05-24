package me.lcode.usecase.unittest;

public class SayHi {
 Adder adder;
 
 public String sayHi(String a, String b){
  adder = new Adder();
  String result = "";
  try {
   result = adder.add(a, b);
  } catch (Exception e) {
   e.printStackTrace();
  }
  
  return result;
 }
 
 public Adder getAdder(){
  return adder;
 }
 
    public void setAdder(Adder a){
  this.adder = a;
 }
}
 class Adder{
	 public String add(String a, String b) throws Exception{
	  return a + " " + b;
	 }
	}