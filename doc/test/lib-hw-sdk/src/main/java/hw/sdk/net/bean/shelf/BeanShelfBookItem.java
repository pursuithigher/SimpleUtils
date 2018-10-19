package hw.sdk.net.bean.shelf;

/**
 * 书架书籍bean
 * @author winzows
 */
public class BeanShelfBookItem {

    /**
     * 书籍id
     */
    private String bookId;
    /**
     * 章节id
     */
    private String chapterId;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }
}
