<template>
  <div class="container mx-auto px-4 h-full">
    <div class="flex content-center items-center justify-center h-full">
      <div class="w-full lg:w-6/12 px-4">
        <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-200 border-0">

          <div class="flex-auto mt-5 px-4 lg:px-10 py-10 pt-0">
            <div class="text-center mb-3">
              <h1 class="text-black text-sm font-bold">Sign up with</h1>
            </div>
            <form v-on:submit.prevent="handleSubmit">
              <div v-if="error" class="text-red-500 text-center mb-3">
                {{ error }}
              </div>

              <div class="relative w-full mb-3">
                <label
                  class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                  for="firstname"
                >
                  First Name
                </label>
                <input
                  id="firstname"
                  v-model="formData.firstName"
                  type="text"
                  class="border-0 px-3 py-3 placeholder-blueGray-300 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                  placeholder="First Name"
                  required
                />
              </div>

              <div class="relative w-full mb-3">
                <label
                  class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                  for="lasname"
                >
                  Last Name
                </label>
                <input
                  id="lastname"
                  v-model="formData.lastName"
                  type="text"
                  class="border-0 px-3 py-3 placeholder-blueGray-300 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                  placeholder="Last Name"
                  required
                />
              </div>

              <div class="relative w-full mb-3">
                <label
                  class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                  for="email"
                >
                  Email
                </label>
                <input
                  id="email"
                  v-model="formData.email"
                  type="email"
                  class="border-0 px-3 py-3 placeholder-blueGray-300 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                  placeholder="Email"
                  required
                />
              </div>

              <div class="relative w-full mb-3">
                <label
                  class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                  for="password"
                >
                  Password
                </label>
                <input
                  id="password"
                  v-model="formData.password"
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
                  {{ loading ? 'Creating Account...' : 'Create Account' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>

import {ref} from 'vue';
import {useRegistration} from '@/composables/useRegistration';


const { loading, error, registerCustomer } = useRegistration();

const formData = ref({
  email: '',
  firstName: '',
  lastName: '',
  password: '',
});

const handleSubmit = async () => {
  try {
    await registerCustomer(formData.value);
  } catch (err) {
    console.error(err);
  }
};


</script>
