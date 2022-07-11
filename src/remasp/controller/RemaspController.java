/*
Copyright (c) 2017, Norman Sutatyo
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. 
 */
 
package remasp.controller;

import remasp.view.RemaspView;

public class RemaspController {

    public RemaspView remaspView;
    private RemaspEditorController editorController;
    private RemaspMachineController machineController;

    public RemaspController() {
        this.remaspView = new RemaspView();
        this.editorController = new RemaspEditorController(remaspView);
        this.machineController = new RemaspMachineController(remaspView);
    }
}
