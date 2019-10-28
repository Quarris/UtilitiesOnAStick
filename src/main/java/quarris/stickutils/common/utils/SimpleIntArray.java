package quarris.stickutils.common.utils;

import net.minecraft.util.IIntArray;

public class SimpleIntArray implements IIntArray {

    protected int[] data;

    public SimpleIntArray(int... data) {
        this.data = data;
    }

    public SimpleIntArray(int size) {
        this.data = new int[size];
    }

    @Override
    public int get(int index) {
        return data[index];
    }

    @Override
    public void set(int index, int value) {
        data[index] = value;
    }

    @Override
    public int size() {
        return data.length;
    }
}
