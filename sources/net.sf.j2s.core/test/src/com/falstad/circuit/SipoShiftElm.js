(function(){var P$=Clazz.newPackage("com.falstad.circuit"),I$=[];
var C$=Clazz.newClass(P$, "SipoShiftElm", null, 'com.falstad.circuit.ChipElm');

C$.$clinit$ = function() {Clazz.load(C$, 1);
}

Clazz.newMeth(C$, '$init0$', function () {
var c;if((c = C$.superclazz) && (c = c.$init0$))c.apply(this);
this.data = 0;
this.clockstate = false;
}, 1);

Clazz.newMeth(C$, '$init$', function () {
this.data = ($s$[0] = 0, $s$[0]);
this.clockstate = false;
}, 1);

Clazz.newMeth(C$, 'hasReset', function () {
return false;
});

Clazz.newMeth(C$, 'c$$I$I', function (xx, yy) {
C$.superclazz.c$$I$I.apply(this, [xx, yy]);
C$.$init$.apply(this);
}, 1);

Clazz.newMeth(C$, 'c$$I$I$I$I$I$java_util_StringTokenizer', function (xa, ya, xb, yb, f, st) {
C$.superclazz.c$$I$I$I$I$I$java_util_StringTokenizer.apply(this, [xa, ya, xb, yb, f, st]);
C$.$init$.apply(this);
}, 1);

Clazz.newMeth(C$, 'getChipName', function () {
return "SIPO shift register";
});

Clazz.newMeth(C$, 'setupPins', function () {
this.sizeX = 9;
this.sizeY = 3;
this.pins = Clazz.array((I$[0]||(I$[0]=Clazz.load(Clazz.load('com.falstad.circuit.ChipElm').Pin))), [this.getPostCount()]);
this.pins[0] = Clazz.new_((I$[0]||(I$[0]=Clazz.load(Clazz.load('com.falstad.circuit.ChipElm').Pin))).c$$I$I$S, [this, null, 1, 2, "D"]);
this.pins[1] = Clazz.new_((I$[0]||(I$[0]=Clazz.load(Clazz.load('com.falstad.circuit.ChipElm').Pin))).c$$I$I$S, [this, null, 2, 2, ""]);
this.pins[1].clock = true;
this.pins[2] = Clazz.new_((I$[0]||(I$[0]=Clazz.load(Clazz.load('com.falstad.circuit.ChipElm').Pin))).c$$I$I$S, [this, null, 1, 0, "I7"]);
this.pins[2].output = true;
this.pins[3] = Clazz.new_((I$[0]||(I$[0]=Clazz.load(Clazz.load('com.falstad.circuit.ChipElm').Pin))).c$$I$I$S, [this, null, 2, 0, "I6"]);
this.pins[3].output = true;
this.pins[4] = Clazz.new_((I$[0]||(I$[0]=Clazz.load(Clazz.load('com.falstad.circuit.ChipElm').Pin))).c$$I$I$S, [this, null, 3, 0, "I5"]);
this.pins[4].output = true;
this.pins[5] = Clazz.new_((I$[0]||(I$[0]=Clazz.load(Clazz.load('com.falstad.circuit.ChipElm').Pin))).c$$I$I$S, [this, null, 4, 0, "I4"]);
this.pins[5].output = true;
this.pins[6] = Clazz.new_((I$[0]||(I$[0]=Clazz.load(Clazz.load('com.falstad.circuit.ChipElm').Pin))).c$$I$I$S, [this, null, 5, 0, "I3"]);
this.pins[6].output = true;
this.pins[7] = Clazz.new_((I$[0]||(I$[0]=Clazz.load(Clazz.load('com.falstad.circuit.ChipElm').Pin))).c$$I$I$S, [this, null, 6, 0, "I2"]);
this.pins[7].output = true;
this.pins[8] = Clazz.new_((I$[0]||(I$[0]=Clazz.load(Clazz.load('com.falstad.circuit.ChipElm').Pin))).c$$I$I$S, [this, null, 7, 0, "I1"]);
this.pins[8].output = true;
this.pins[9] = Clazz.new_((I$[0]||(I$[0]=Clazz.load(Clazz.load('com.falstad.circuit.ChipElm').Pin))).c$$I$I$S, [this, null, 8, 0, "I0"]);
this.pins[9].output = true;
});

Clazz.newMeth(C$, 'getPostCount', function () {
return 10;
});

Clazz.newMeth(C$, 'getVoltageSourceCount', function () {
return 8;
});

Clazz.newMeth(C$, 'execute', function () {
if (this.pins[1].value && !this.clockstate ) {
this.clockstate = true;
this.data = ($s$[0] = ((this.data >>> 1)|0), $s$[0]);
if (this.pins[0].value) this.data = ($s$[0] = this.data+(128), $s$[0]);
if ((this.data & 128) > 0) this.pins[2].value = true;
 else this.pins[2].value = false;
if ((this.data & 64) > 0) this.pins[3].value = true;
 else this.pins[3].value = false;
if ((this.data & 32) > 0) this.pins[4].value = true;
 else this.pins[4].value = false;
if ((this.data & 16) > 0) this.pins[5].value = true;
 else this.pins[5].value = false;
if ((this.data & 8) > 0) this.pins[6].value = true;
 else this.pins[6].value = false;
if ((this.data & 4) > 0) this.pins[7].value = true;
 else this.pins[7].value = false;
if ((this.data & 2) > 0) this.pins[8].value = true;
 else this.pins[8].value = false;
if ((this.data & 1) > 0) this.pins[9].value = true;
 else this.pins[9].value = false;
}if (!this.pins[1].value) this.clockstate = false;
});

Clazz.newMeth(C$, 'getDumpType', function () {
return 189;
});
var $s$ = new Int16Array(1);

Clazz.newMeth(C$);
})();
//Created 2017-12-17 19:28:22