package me.lauriichan.minecraft.wildcard.core.data.setting;

final class NullSetting implements ISetting {

    public static final NullSetting NULL = new NullSetting();

    private NullSetting() {}

    @Override
    public String getName() {
        return "null";
    }

    @Override
    public String getCategory() {
        return "null";
    }

    @Override
    public Class<?> getType() {
        return Void.class;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public NullSetting setPersistent(final boolean persistent) {
        return this;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public Object get() {
        return null;
    }

    @Override
    public boolean set(final Object value) {
        return false;
    }

}
