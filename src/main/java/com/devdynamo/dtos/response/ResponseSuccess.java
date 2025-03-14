package com.devdynamo.dtos.response;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;


public class ResponseSuccess extends ResponseEntity<ResponseSuccess.Payload> {
    public ResponseSuccess(HttpStatusCode status, String message) {
        super(new Payload(status.value(), message), HttpStatus.OK);
    }

    public ResponseSuccess(HttpStatusCode status, String message, Object data) {
        super(new Payload(status.value(), message, data), HttpStatus.OK);
    }

    @Getter
    public static class Payload{
        private final int status;
        private final String message;
        private Object data;

        public Payload(int status, String message) {
            this.message = message;
            this.status = status;
        }

        public Payload(int status, String message, Object data) {
            this.data = data;
            this.message = message;
            this.status = status;
        }
    }
}
