package revolhope.splanes.com.mygrocery.data.model.item;

import androidx.annotation.Nullable;

public class Filter {

    private String[] data;
    private int index;

    public Filter(String[] data, @Nullable Integer filterIndex) {
        this.data = data;
        this.index = filterIndex == null ? 0 : filterIndex;
    }

    public int getIndex() { return this.index; }

    public String getString() {
        return data[index];
    }

    public void setIndex(int filterIndex) {
        this.index = filterIndex;
    }
}
