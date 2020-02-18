package kr.hs.emirim.jiwonan.retrofittest;

public class LoginResponse {
    private boolean success;
    private String reason;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", reason='" + reason + '\'' +
                '}';
    }
}
