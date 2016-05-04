from twisted.internet.defer import inlineCallbacks
from twisted.logger import Logger

from autobahn.twisted.util import sleep
from autobahn.twisted.wamp import ApplicationSession
from autobahn.wamp.exception import ApplicationError

class AppSession(ApplicationSession):
    @inlineCallbacks
    def onJoin(self, details):
        print("session ready")

        def recognize(img):
        	# return dummy netlist
            return ("$ 1 0.000005 14.841315910257658 48 5 50\n"
           		   "v 112 240 112 160 0 1 35 5 0 0 0.5\n"
           		   "r 112 160 464 160 0 10\n"
           		   "c 464 160 464 240 0 0.000015 22.122690509926905\n"
           		   "l 112 240 464 240 0 1 0.022218800709066414\n"
           		   "o 2 64 0 35 40 0.2 0 -1")

        try:
            # register recognize procedure
            yield self.register(recognize, u'ph.edu.msuiit.circuitlens.recognize')
            print("procedures registered")
        except Exception as e:
            print("could not register procedure: {0}".format(e))