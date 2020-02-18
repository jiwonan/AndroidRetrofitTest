package kr.hs.emirim.jiwonan.retrofittest;

import okhttp3.MultipartBody;

public class Article {
    private int id;
    private String contents;
    private int heart;
    private MultipartBody.Part file;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public MultipartBody.Part getFile() {
        return file;
    }

    public void setFile(MultipartBody.Part file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", contents='" + contents + '\'' +
                ", heart=" + heart +
                ", file=" + file +
                '}';
    }
}
