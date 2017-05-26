package cn.edu.shou.missive.domain.missiveDataForm;


import java.util.List;

public class CommentContentFrom extends BaseEntityForm {
    public long contentId;
    public String Base30url;
    public String Imageurl;

    public String contentText;
    public List<UserFrom> ContentUsers;   //

    public boolean enabled;

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getBase30url() {
        return Base30url;
    }

    public void setBase30url(String base30url) {
        Base30url = base30url;
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public List<UserFrom> getContentUsers() {
        return ContentUsers;
    }

    public void setContentUsers(List<UserFrom> contentUsers) {
        ContentUsers = contentUsers;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
