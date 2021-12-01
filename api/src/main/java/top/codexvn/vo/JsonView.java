package top.codexvn.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain=true)
public class JsonView <T>{
    private long code;
    private T result;
    private  String message;
    private JsonView(T result) {
        this.result = result;
    }
    public static <U> JsonView<U> getSuccessJsonView(U result){
        return  new JsonView<U>(0,result,"success");
    }
    public static <U> JsonView<U> getErrorJsonView(U result){
        return  new JsonView<U>(1,result,"error");
    }
    public static <U> JsonView<U> getSuccessJsonView(U result,String message){
        return  new JsonView<U>(0,result,message);
    }
    public static <U> JsonView<U> getErrorJsonView(U result,String message){
        return  new JsonView<U>(1,result,message);
    }
    public static  JsonView<Object> getSuccessJsonView(){
        return  JsonView.getSuccessJsonView(null);
    }
    public static  JsonView<Object> getErrorJsonView(){
        return   JsonView.getErrorJsonView(null);
    }
}

