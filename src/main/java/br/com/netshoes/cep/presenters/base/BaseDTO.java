package br.com.netshoes.cep.presenters.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseDTO<T> {

    private T data;

    public BaseDTO(T data) {
        this.data = data;
    }

}
