package hw.sdk.net.bean.seach;

import org.json.JSONObject;

import java.util.List;

import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;

/**
 * 建议bean
 * @author caimantang on 2018/4/13.
 */

public class BeanSuggest extends HwPublicBean<BeanSuggest> {
    /**
     * 建议item
     */
    public List<SuggestItem> list;

    @Override
    public BeanSuggest parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (isSuccess()) {
            this.list = JsonUtils.getSuggestItemList(jsonObj.optJSONArray("data"));
        }
        return this;
    }

    @Override
    public String toString() {
        return "SuggestBean{"
                +
                "list=" + list
                +
                '}';
    }


    public boolean isAvailable() {
        return null != list && list.size() > 0;
    }
}
