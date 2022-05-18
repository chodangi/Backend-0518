package MCcrew.Coinportal.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto<T> extends BasicResponse{
    private int count;
    private T data;
}
