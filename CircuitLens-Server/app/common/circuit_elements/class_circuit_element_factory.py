from class_capacitor import Capacitor
from class_resistor import Resistor
from class_inductor import Inductor
from class_toggle_switch import ToggleSwitch
from class_wire import Wire
from class_current_source import CurrentSource
from class_voltage_source import VoltageSource
from class_null_circuit_element import NullCircuitElement

class CircuitElementFactory:
    @staticmethod
    def create(elemType, value=0):
        if 'voltage_source' == elemType:
            return VoltageSource(value)
        elif 'current_source' == elemType:
            return CurrentSource(value)
        elif 'capacitor' == elemType:
            return Capacitor(value)
        elif 'resistor' == elemType:
            return Resistor(value)
        elif 'inductor' == elemType:
            return Inductor(value)
        elif 'toggle_switch' == elemType:
            return ToggleSwitch(value)
        elif 'wire' == elemType:
            return Wire(value)
        else:
            return NullCircuitElement(value)
            