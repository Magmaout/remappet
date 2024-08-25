package magmaout.mappet.client.gui.scripts.scriptedItem.util;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Textbox {
    private String text = "";
    private Consumer<String> callback;

    private int cursor;
    private int selection = -1;
    private int left;
    private int right;

    private Predicate<String> validator;
    private int length = 100;
    private boolean focused;
    private boolean enabled = true;
    private boolean visible = true;

    private IKey placeholder = IKey.EMPTY;
    private boolean background = true;
    private int color = 0xffffffff;
    private boolean border;

    private boolean holding;
    private int lastX;

    public Area area = new Area();
    public FontRenderer font;

    private int lastW;

    private long lastClick;

    public Textbox(Consumer<String> callback) {
        this.callback = callback;
    }

    public void setFont(FontRenderer font) {
        this.font = font;

        this.updateBounds(false);
    }

    public FontRenderer getFont() {
        return this.font;
    }

    public void setPlaceholder(IKey placeholder) {
        this.placeholder = placeholder;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    /* Text */

    public String getSelectedText() {
        if (this.isSelected()) {
            int min = Math.min(this.cursor, this.selection);
            int max = Math.max(this.cursor, this.selection);

            return this.text.substring(min, max);
        }

        return "";
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        if (text.length() > this.length) {
            text = text.substring(0, this.length);
        }

        this.text = text;

        this.moveCursorToStart();
        this.deselect();
        this.updateBounds(false);
    }

    public void acceptText() {
        if (this.callback != null) {
            this.callback.accept(this.text);
        }
    }

    public void insert(String text) {
        this.deleteSelection();

        text = text.replaceAll("\n", "");

        int i = this.text.length() + text.length();

        if (i >= this.length) {
            text = text.substring(0, this.length - this.text.length());
        }

        if (text.isEmpty()) {
            return;
        }

        String newText = this.text;

        if (this.cursor == 0) {
            newText = text + newText;
        } else if (this.cursor >= newText.length()) {
            newText += text;
        } else {
            newText = newText.substring(0, this.cursor) + text + newText.substring(this.cursor);
        }

        this.text = newText;
        this.moveCursorTo(this.cursor + text.length());

        this.updateBounds(false);
    }

    public void deleteCharacter() {
        if (this.cursor > 0) {
            this.text = this.text.substring(0, this.cursor - 1) + this.text.substring(this.cursor);
            this.moveCursorBy(-1);
        }
    }

    public void setValidator(Predicate<String> validator) {
        this.validator = validator;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        if (this.text.length() > length) {
            this.text = this.text.substring(0, length);
            this.updateBounds(false);
        }

        this.length = length;
    }

    /* Cursor, selection and offsets */

    public int getCursor() {
        return this.cursor;
    }

    public int getSelection() {
        return this.selection;
    }

    public boolean selectGroup(int direction, boolean select) {
        Pair<Integer, Integer> groups = this.findGroup(direction, this.cursor);

        if (groups == null) {
            return false;
        }

        int min = groups.a;
        int max = groups.b;

        if (select) {
            if (direction == 0) {
                this.cursor = max;
                this.selection = min;
            } else {
                if (!this.isSelected()) {
                    this.selection = this.cursor;
                }

                this.cursor = direction < 0 ? min : max;
            }
        } else {
            this.deselect();
            this.cursor = direction < 0 ? min : max;
        }

        this.updateBounds(false);

        return true;
    }

    /**
     * Find a group (two cursors) at given offset
     */
    public Pair<Integer, Integer> findGroup(int direction, int offset) {
        StringGroupMatcher matcher = new StringGroupMatcher();

        return matcher.findGroup(direction, this.text, offset);
    }

    public void moveCursorTo(int cursor) {
        this.cursor = cursor;
        this.cursor = MathUtils.clamp(this.cursor, 0, this.text.length());

        this.updateBounds(false);
    }

    public void moveCursorToStart() {
        this.moveCursorTo(0);
    }

    public void moveCursorToEnd() {
        this.moveCursorTo(this.text.length());
    }

    private void moveCursorBy(int i) {
        this.moveCursorTo(this.cursor + (int) Math.copySign(1, i));
    }

    public boolean isSelected() {
        return this.selection != this.cursor && this.selection >= 0;
    }

    public void setSelection(int selection) {
        this.selection = selection;

        this.updateBounds(true);
    }

    public void deselect() {
        this.selection = -1;
    }

    public void deleteSelection() {
        if (this.cursor == this.selection) {
            this.deselect();
        }

        if (!this.isSelected()) {
            return;
        }

        int min = Math.min(this.cursor, this.selection);
        int max = Math.max(this.cursor, this.selection);

        this.text = this.text.substring(0, min) + this.text.substring(max);

        this.deselect();

        this.cursor = min;

        this.updateBounds(false);
        this.clamp();
    }

    private void updateBounds(boolean selection) {
        int cursor = selection ? this.selection : this.cursor;
        int length = this.text.length();
        int offset = this.background ? 10 : 0;
        int max = this.area.w - offset;

        if (this.font.getStringWidth(this.text) < max) {
            this.left = 0;
            this.right = length;

            return;
        }

        if (cursor < this.left) {
            int bound = this.getBound(max, cursor, 1);

            if (bound == cursor) {
                bound = this.getBound(max, length - 1, -1);

                this.left = bound;
                this.right = length;
            } else {
                this.left = cursor;
                this.right = MathUtils.clamp(bound + 1, 0, length);
            }
        } else if (cursor >= this.right) {
            int bound = this.getBound(max, MathUtils.clamp(cursor, 0, length - 1), -1);

            if (bound == cursor) {
                bound = this.getBound(max, 0, 1);

                this.left = 0;
                this.right = bound;
            } else {
                this.left = bound;
                this.right = cursor;
            }
        }

        this.left = MathUtils.clamp(this.left, 0, length);
        this.right = MathUtils.clamp(this.right, 0, length);
    }

    private int getBound(int max, int start, int direction) {
        int w = 0;

        for (int i = start; i >= 0 && i < this.text.length(); i += direction) {
            int sw = this.font.getCharWidth(this.text.charAt(i));

            if (w < max && w + sw >= max) {
                return i;
            }

            w += sw;
        }

        return start;
    }

    private void clamp() {
        this.cursor = MathUtils.clamp(this.cursor, 0, this.text.length());
        this.selection = MathUtils.clamp(this.selection, -1, this.text.length());
    }

    private String getWrappedText() {
        int length = this.text.length();

        return this.text.substring(
                MathUtils.clamp(this.left, 0, length),
                MathUtils.clamp(this.right, 0, length)
        );
    }

    /* Visual */

    public boolean hasBackground() {
        return this.background;
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /* Input handling */

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public void mouseClicked(int x, int y, int button) {
        if (button == 0 && this.area.isInside(x, y)) {
            if (System.currentTimeMillis() < this.lastClick) {
                this.selectGroup(0, true);
                this.lastClick -= 500;
            } else {
                int lastSelection = this.selection;

                this.deselect();

                if (GuiScreen.isShiftKeyDown()) {
                    this.selection = lastSelection < 0 ? this.cursor : lastSelection;
                }

                this.focused = true;
                this.lastX = x;
                this.holding = true;
                this.moveCursorTo(this.getIndexAt(x));

                this.lastClick = System.currentTimeMillis() + 200;
            }
        } else {
            this.focused = false;
        }
    }

    public void mouseReleased(int x, int y, int button) {
        if (button == 0) {
            this.holding = false;
        }
    }

    private int getIndexAt(int x) {
        x -= this.area.x;

        if (this.background) {
            x -= 4;
        }

        if (x >= 0) {
            String wrappedText = this.getWrappedText();
            int w = this.font.getStringWidth(wrappedText);

            if (x >= w) {
                return this.right;
            } else {
                w = 0;

                for (int i = 0, c = wrappedText.length(); i < c; i++) {
                    char character = wrappedText.charAt(i);

                    if (character == '\u00A7') {
                        i += 1;

                        continue;
                    }

                    int string = this.font.getCharWidth(character);

                    if (x >= w && x < w + string) {
                        return this.left + i;
                    }

                    w += string;
                }
            }
        }

        return this.left;
    }

    public boolean keyPressed(GuiContext context) {
        if (!this.focused || !this.enabled || !this.visible) {
            return false;
        }

        boolean selecting = this.isSelected();
        boolean ctrl = GuiScreen.isCtrlKeyDown();
        boolean shift = GuiScreen.isShiftKeyDown();

        if (ctrl && (context.keyCode == Keyboard.KEY_C || context.keyCode == Keyboard.KEY_X)) {
            if (selecting) {
                GuiScreen.setClipboardString(this.getSelectedText());

                if (context.keyCode == Keyboard.KEY_X) {
                    this.deleteSelection();
                }

                return true;
            }
        } else if (ctrl && context.keyCode == Keyboard.KEY_V) {
            String clipboard = GuiScreen.getClipboardString();

            if (!clipboard.isEmpty()) {
                this.insert(clipboard);
                this.acceptText();
            }

            return true;
        } else if (ctrl && context.keyCode == Keyboard.KEY_A) {
            this.selection = 0;
            this.cursor = this.text.length();
            this.updateBounds(false);

            return true;
        } else if (context.keyCode == Keyboard.KEY_HOME) {
            this.handleShift(shift);
            this.moveCursorToStart();
        } else if (context.keyCode == Keyboard.KEY_END) {
            this.handleShift(shift);
            this.moveCursorToEnd();
        } else if (context.keyCode == Keyboard.KEY_LEFT || context.keyCode == Keyboard.KEY_RIGHT) {
            int offset = context.keyCode == Keyboard.KEY_LEFT ? -1 : 1;

            if (ctrl) {
                if (!this.selectGroup(offset, shift)) {
                    this.handleShift(shift);
                    this.moveCursorBy(offset);
                }
            } else {
                this.handleShift(shift);
                this.moveCursorBy(offset);
            }
        } else if (context.keyCode == Keyboard.KEY_BACK || context.keyCode == Keyboard.KEY_DELETE) {
            if (this.isSelected()) {
                this.deleteSelection();
                this.acceptText();

                return true;
            } else if (context.keyCode == Keyboard.KEY_DELETE && this.cursor < this.text.length()) {
                this.moveCursorBy(1);
                this.deleteCharacter();
                this.acceptText();

                return true;
            } else if (context.keyCode == Keyboard.KEY_BACK) {
                this.deleteCharacter();
                this.acceptText();

                return true;
            }
        }

        return false;
    }

    public boolean textInput(char character) {
        if (!this.focused || !this.enabled || !this.visible) {
            return false;
        }

        if (ChatAllowedCharacters.isAllowedCharacter(character)) {
            String text = String.valueOf(character);

            if (this.validator != null && !this.validator.test(text)) {
                return false;
            }

            this.insert(text);
            this.acceptText();

            return true;
        }

        return false;
    }

    private void handleShift(boolean shift) {
        if (shift) {
            if (this.selection == -1) {
                this.selection = cursor;
            }
        } else {
            this.deselect();
        }
    }

    /* Rendering */

    public void render(GuiContext context) {
        if (!this.visible) {
            return;
        }

        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (this.lastW != this.area.w) {
            this.lastW = this.area.w;
            this.updateBounds(false);
        }

        if (this.area.isInside(mouseX, mouseY) && this.holding && Math.abs(mouseX - this.lastX) > 2) {
            this.moveCursorTo(this.getIndexAt(mouseX));
        }

        int x = this.area.x;
        int y = this.area.y;

        if (this.background) {
            this.area.draw(0xff000000);

            if (this.border) {
                int borderColor = this.focused ? 0xff000000 + McLib.primaryColor.get() : 0xffaaaaaa;

                GuiDraw.drawOutline(this.area.x, this.area.y, this.area.ex(), this.area.ey(), borderColor);
            }

            x = this.area.x + 4;
            y = this.area.my() - this.font.FONT_HEIGHT / 2;
        }

        boolean empty = !this.focused && this.text.isEmpty();
        String text = empty ? this.placeholder.get() : this.getWrappedText();
        int length = text.length();
        int color = empty ? 0xaaaaaa : this.color;

        if (!empty && this.isSelected()) {
            int min = MathUtils.clamp(Math.min(this.cursor, this.selection) - this.left, 0, length);
            int max = MathUtils.clamp(Math.max(this.cursor, this.selection) - this.left, 0, length);

            int offset = this.font.getStringWidth(text.substring(0, min));
            int sx = x + offset;
            int sw = this.font.getStringWidth(text.substring(min, max));

            Gui.drawRect(sx, y - 2, sx + sw, y + this.font.FONT_HEIGHT + 2, 0x88000000 + McLib.primaryColor.get());
        }

        context.font.drawStringWithShadow(text, x, y, color);

        if (this.focused) {
            int relativeIndex = this.cursor - this.left;

            if (relativeIndex >= 0 && relativeIndex <= length) {
                x += this.font.getStringWidth(text.substring(0, relativeIndex));

                float alpha = (float) Math.sin(context.partialTicks / 2D);
                int c = ColorUtils.setAlpha(0xffffff, alpha * 0.5F + 0.5F);

                Gui.drawRect(x, y - 1, x + 1, y + this.font.FONT_HEIGHT + 1, c);
            }
        }
    }
}