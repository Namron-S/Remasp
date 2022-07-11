/*
Copyright (c) 2017, Norman Sutatyo
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. 
 */
package remasp.model;

public class KeinGueltigerBefehlException extends Exception {

    private int startOffset, endOffset;

    public int getStartOffset() {
        return this.startOffset;
    }

    public int getEndOffset() {
        return this.endOffset;
    }

    public KeinGueltigerBefehlException(String string, int startOffset, int endOffset) {
        super(string);
        this.startOffset = startOffset;
        this.endOffset = endOffset;

    }

}
