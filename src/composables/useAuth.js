// composables/useAuth.js
import {computed, ref} from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

export const useAuth = (options = {}) => {  // Accept options as parameter
    const email = ref('')
    const password = ref('')
    const loading = ref(false)
    const error = ref(null)
    const router = useRouter()
    const store = useStore()
    const {
        loginRedirect = '/kunde/dashboard',
        logoutRedirect = '/auth/login'
    } = options

    const login = async () => {
        loading.value = true
        error.value = null

        try {
            await store.dispatch('auth/login', {
                email: email.value,
                password: password.value
            })
            router.push(loginRedirect)
        } catch (e) {
            if (e.response) {
                error.value = e.response.data.error
            }
        } finally {
            loading.value = false
        }
    }


    const logout = async () => {
        try {
            await store.dispatch('auth/logout')
            router.push(logoutRedirect)
        } catch (e) {
            if (e.response) {
                error.value = e.response.data.error
            }
        }
    }
    const checkAuth = async () => {
        try {
            return await store.dispatch('auth/checkAuthentication')
        } catch (e) {
            if (e.response) {
                error.value = e.response.data.error
            }
            return false
        }
    }
    // Add computed properties to access store state
    const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
    const userProfile = computed(() => store.getters['auth/userProfile'])

    return {
        email,
        password,
        loading,
        error,
        login,
        logout,
        checkAuth,
        isAuthenticated,
        userProfile
    }
}