// store/modules/auth.js
import axios from 'axios'
import api from "@/utils/axios-auth";

const state = {
    user: JSON.parse(localStorage.getItem('user')) || null
}

const getters = {
    isAuthenticated: state => !!state.user,
    userProfile: state => state.user,
    userRoles: state => state.user ? state.user.roles : []
}

const actions = {
    async login({ commit }, credentials) {
        try {
            const response = await api.post('/auth/login', credentials)
            const user = response.data
            localStorage.setItem('user', JSON.stringify(user))


            commit('SET_AUTH_SUCCESS', { user })
            return response
        } catch (error) {
            commit('SET_AUTH_ERROR')
            localStorage.removeItem('user')
            throw error
        }
    },

    async logout({ commit }) {
        try {
            await api.post('/auth/signout')

            localStorage.removeItem('user')

            delete axios.defaults.headers.common['Authorization']

            commit('SET_LOGOUT')
        } catch (error) {
            console.error('Logout error:', error)
            throw error
        }
    },
    async checkAuthentication({ commit }) {
        try {
            const response = await api.get('/auth/authenticated')
            return response.data
        } catch (error) {
            commit('SET_AUTH_ERROR')
            return false
        }
    },
    async refreshToken({ commit }, credentials) {
        try {
            return await api.post('/auth/refreshtoken', credentials)
        } catch (error) {
            commit('SET_AUTH_ERROR')
            localStorage.removeItem('user')
            throw error
        }
    },
    async fetchUser({ commit }) {
        try {
            // Replace with your API endpoint
            const response = await api.get('/auth/user')
            const user = response.data

            localStorage.setItem('user', JSON.stringify(user))
            commit('SET_USER', user)
            return user
        } catch (error) {
            commit('SET_AUTH_ERROR')
            throw error
        }
    }
}

const mutations = {
    SET_AUTH_SUCCESS(state, {  user }) {
        state.user = user
    },

    SET_AUTH_ERROR(state) {
        state.user = null
    },

    SET_LOGOUT(state) {
        state.user = null
    },

    SET_USER(state, user) {
        state.user = user
    }
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}