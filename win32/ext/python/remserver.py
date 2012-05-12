import sys
if len(sys.argv) > 2:
    sys.path.insert(0, sys.argv[2])

from rpyc.utils.server import Server
from rpyc.utils.classic import DEFAULT_SERVER_PORT
from rpyc.core import SlaveService
from rpyc.utils.logger import Logger
import threading

__traceable__ = 0

class SimpleServer(Server):
    def _get_logger(self):
        return Logger(self.service.get_service_name(), console = None, quiet = True)

    def _accept_method(self, sock):
        self._serve_client(sock, None)

class ModSlaveService(SlaveService):
    __slots__ = []

    def on_connect(self):
        import imp
        from rpyc.core.service import ModuleNamespace

        sys.modules["__oldmain__"] = sys.modules["__main__"]
        sys.modules["__main__"] = imp.new_module("__main__")
        self.exposed_namespace = sys.modules["__main__"].__dict__

        self._conn._config.update(dict(
            allow_all_attrs = True,
            allow_pickle = True,
            allow_getattr = True,
            allow_setattr = True,
            allow_delattr = True,
            import_custom_exceptions = True,
            instantiate_custom_exceptions = True,
            instantiate_oldstyle_exceptions = True,
        ))
        # shortcuts
        self._conn.modules = ModuleNamespace(self._conn.root.getmodule)
        self._conn.eval = self._conn.root.eval
        self._conn.execute = self._conn.root.execute
        self._conn.namespace = self._conn.root.namespace
        if sys.version_info[0] > 2:
            self._conn.builtin = self._conn.modules.builtins
        else:
            self._conn.builtin = self._conn.modules.__builtin__

def main():
    import warnings
    warnings.simplefilter("ignore", DeprecationWarning)

    try:
        port = int(sys.argv[1])
    except:
        port = DEFAULT_SERVER_PORT

    t = SimpleServer(ModSlaveService, port = port, auto_register = False)
    t.start()

if __name__ == "__main__":
    main()


