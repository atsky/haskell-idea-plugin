package org.jetbrains.haskell.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.LazyRangeMarkerFactory;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

public final class LineColPosition implements Comparable<LineColPosition> {
    public final int myLine;
    public final int myColumn;

    public LineColPosition(int line, int column) {
        this.myLine = line;
        this.myColumn = column;
    }

    public static LineColPosition parse(String str) {
        int line;
        int col;
        if ("?".equals(str)) {
            line = 0;
            col = 0;
        } else {
            int p = str.indexOf(':');
            if (p < 0)
                return null;
            String strLine = str.substring(0, p);
            String strCol = str.substring(p + 1);
            line = parseInt(strLine);
            col = parseInt(strCol);
        }
        return new LineColPosition(line, col);
    }

    private static int parseInt(String str) {
        if ("?".equals(str))
            return 0;
        return Integer.parseInt(str);
    }

    public int getOffset(PsiFile file) {
        return getOffset(file, myLine, myColumn);
    }

    public static int getOffset(PsiFile psiFile, int line, int col) {
        LazyRangeMarkerFactory factory = LazyRangeMarkerFactory.getInstance(psiFile.getProject());
        RangeMarker rangeMarker = factory.createRangeMarker(
            psiFile.getVirtualFile(), Math.max(0, line - 1), Math.max(0, col - 1), false
        );
        return rangeMarker.getStartOffset();
    }

    public static LineColPosition fromOffset(PsiFile psiFile, int offset) {
        VirtualFile file = psiFile.getVirtualFile();
        if (file == null)
            return null;
        FileDocumentManager fdm = FileDocumentManager.getInstance();
        Document doc = fdm.getCachedDocument(file);
        if (doc == null)
            return null;
        int line = doc.getLineNumber(offset);
        int col = offset - doc.getLineStartOffset(line);
        return new LineColPosition(line + 1, col + 1);
    }

    @Override
    public String toString() {
        return myLine + ":" + myColumn;
    }

    public int compareTo(LineColPosition that) {
        if (this.myLine < that.myLine) {
            return -1;
        } else if (this.myLine > that.myLine) {
            return 1;
        } else {
            if (this.myColumn < that.myColumn) {
                return -1;
            } else if (this.myColumn > that.myColumn) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LineColPosition) {
            LineColPosition that = (LineColPosition) obj;
            return this.myLine == that.myLine && this.myColumn == that.myColumn;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 31 * myLine + myColumn;
    }
}
