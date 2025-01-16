<template>
  <div class="container mx-auto px-4 h-full">
    <div class="flex content-center items-center justify-center h-full">
      <div class="w-full lg:w-4/12 px-4">
        <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-200 border-0">

          <div class="flex-auto mt-5 px-4 lg:px-10 py-10 pt-0">
            <div class="text-black text-center mb-3 font-bold">
              <h1>Sign in with</h1>
            </div>
            <form @submit.prevent="handleLogin">
              <div v-if="error" class="text-red-500 text-center mb-3">
                {{ error }}
              </div>
              <div class="relative w-full mb-3">
                <label
                    class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                    htmlFor="email"
                >
                  Email
                </label>
                <input
                    id="email"
                    v-model="email"
                    type="email"
                    class="border-0 px-3 py-3 placeholder-blueGray-300 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                    placeholder="Email"
                    required
                />
              </div>

              <div class="relative w-full mb-3">
                <label
                    class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                    htmlFor="password"
                >
                  Password
                </label>
                <input
                    id="password"
                    v-model="password"
                    type="password"
                    class="border-0 px-3 py-3 placeholder-blueGray-300 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                    placeholder="Password"
                    required
                />
              </div>


              <div class="text-center mt-6">
                <button
                    :disabled="loading"
                    class="bg-blueGray-800 text-white active:bg-blueGray-600 text-sm font-bold uppercase px-6 py-3 rounded shadow hover:shadow-lg outline-none focus:outline-none mr-1 mb-1 w-full ease-linear transition-all duration-150"
                    type="submit"
                >
                  {{ loading ? 'Signing in...' : 'Sign In' }}
                </button>
              </div>
            </form>
          </div>
        </div>
        <div class="flex flex-wrap mt-6 relative">
          <div class="w-1/2">
            <a href="javascript:void(0)" class="text-blueGray-200">
              <small>Forgot password?</small>
            </a>
          </div>
          <div class="w-1/2 text-right">
            <router-link to="/auth/register" class="text-blueGray-200">
              <small>Create new account</small>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/* eslint-disable no-unused-vars */
import { useAuth } from '@/composables/useAuth'

const { email, password, loading, error, login } = useAuth()

const handleLogin = async () => {
  try {
    await login()
  } catch (err) {
    console.error('Login failed:', err)
  }
}
</script>