from class_switch import Swicth

class ToggleSwitch(Swicth):
    def toggle(self):
        self._state = not self._state