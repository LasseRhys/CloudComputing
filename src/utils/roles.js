import {computed} from "vue";
import store from "@/store";

export const hasEventManagerRole = computed(() => {
    const userRoles = store.getters['auth/userRoles']
    console.log(userRoles)
    return Array.isArray(userRoles) ?
        userRoles.includes('ROLE_EVENT_MANAGER') :
        (userRoles && userRoles.valueOf ?
            userRoles.valueOf().includes('ROLE_EVENT_MANAGER') :
            false)
})

export const hasEventCreatorRole = computed(() => {
    const userRoles = store.getters['auth/userRoles']
    return Array.isArray(userRoles) ?
        userRoles.includes('ROLE_EVENT_CREATOR') :
        (userRoles && userRoles.valueOf ?
            userRoles.valueOf().includes('ROLE_EVENT_CREATOR') :
            false)
})