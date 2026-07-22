package ecommerceai.dto.response;

public class ApiResponse<T>{


        private boolean success;

        private String message;

        private T data;

        public ApiResponse() {
        }

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // getters and setters
        void setSuccess(boolean success) {
            this.success = success;
        }
        void setMessage(String message) {
            this.message = message;
        }
        void setData(T data) {
            this.data = data;
        }
        boolean getSuccess() {
            return success;
        }
        String getMessage() {
            return message;
        }
        T getData() {
            return data;
        }

}
