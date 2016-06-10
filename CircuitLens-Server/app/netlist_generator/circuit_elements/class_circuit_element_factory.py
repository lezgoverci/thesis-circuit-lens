import class_capacitor as c
import class_resistor as r
import class_inductor as i
import class_toggle_switch as ts
import class_wire as w
import class_current_source as cs
import class_voltage_source as vs
import class_null_circuit_element as nce

class CircuitElementFactory:
    @staticmethod
    def create(elemType, value=0):
        if 'voltage_source' == elemType:
            return vs.VoltageSource(value)
        elif 'current_source' == elemType:
            return cs.CurrentSource(value)
        elif 'capacitor' == elemType:
            return c.Capacitor(value)
        elif 'resistor' == elemType:
            return r.Resistor(value)
        elif 'inductor' == elemType:
            return i.Inductor(value)
        elif 'toggle_switch' == elemType:
            return ts.ToggleSwitch(value)
        elif 'wire' == elemType:
            return w.Wire(value)
        else:
            return nce.NullCircuitElement(value)
            