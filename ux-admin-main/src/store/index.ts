import { storeToRefs } from "pinia";

import { useUser } from "./modules/user";
import { useAuth } from "./modules/auth";
import { useGlobalStore } from "./modules/global";
import { useKeepAlive } from "./modules/keepAlive";
import { useTabs } from "./modules/tabs";

export { storeToRefs, useUser, useAuth, useGlobalStore, useKeepAlive, useTabs };
