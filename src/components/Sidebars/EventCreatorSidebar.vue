<template>
  <nav
      class="md:left-0 md:block md:fixed md:top-0 md:bottom-0 md:overflow-y-auto md:flex-row md:flex-nowrap md:overflow-hidden shadow-xl bg-white flex flex-wrap items-center justify-between relative md:w-64 z-10 py-4 px-6"
  >
    <div
        class="md:flex-col md:items-stretch md:min-h-full md:flex-nowrap px-0 flex flex-wrap items-center justify-between w-full mx-auto"
    >
      <!-- Menü-Schalter -->
      <button
          class="cursor-pointer text-black opacity-50 md:hidden px-3 py-1 text-xl leading-none bg-transparent rounded border border-solid border-transparent"
          type="button"
          v-on:click="toggleCollapseShow('bg-white m-2 py-3 px-6')"
      >
        <i class="fas fa-bars"></i>
      </button>
      <!-- Marke -->
      <router-link
          class="md:block text-left md:pb-2 text-blueGray-600 mr-0 inline-block whitespace-nowrap text-xl font-bold p-4 px-0"
          to="event-creator/dashboard"
      >
        EventTom
      </router-link>
      <!-- Benutzer -->
      <ul class="md:hidden items-center flex flex-wrap list-none">
        <li class="inline-block relative">
          <notification-dropdown />
        </li>
        <li class="inline-block relative">
          <user-dropdown />
        </li>
      </ul>
      <!-- Ausklappbereich -->
      <div
          class="md:flex md:flex-col md:items-stretch md:opacity-100 md:relative md:mt-4 md:shadow-none shadow absolute top-0 left-0 right-0 z-40 overflow-y-auto overflow-x-hidden h-auto items-center flex-1 rounded"
          v-bind:class="collapseShow"
      >
        <!-- Ausklapp-Header -->
        <div
            class="md:min-w-full md:hidden block pb-4 mb-4 border-b border-solid border-blueGray-200"
        >
          <div class="flex flex-wrap">
            <div class="w-6/12">
              <router-link
                  class="md:block text-left md:pb-2 text-blueGray-600 mr-0 inline-block whitespace-nowrap text-xl font-bold p-4 px-0"
                  to="/event-creator/dashboard"
              >
                EventTom
              </router-link>
            </div>
            <div class="w-6/12 flex justify-end">
              <button
                  type="button"
                  class="cursor-pointer text-black opacity-50 md:hidden px-3 py-1 text-xl leading-none bg-transparent rounded border border-solid border-transparent"
                  v-on:click="toggleCollapseShow('hidden')"
              >
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- Trennlinie -->
        <hr class="my-4 md:min-w-full" />
        <!-- Überschrift -->
        <h6
            class="md:min-w-full text-blueGray-500 text-xs uppercase font-bold block pt-1 pb-4 no-underline"
        >
          Mitarbeiterbereich
        </h6>
        <!-- Navigation -->

        <ul class="md:flex-col md:min-w-full flex flex-col list-none">

          <!-- Dashboard -->
          <li class="items-center">
            <router-link
                to="/event-creator/dashboard"
                v-slot="{ href, navigate, isActive }"
            >
              <a
                  :href="href"
                  @click="navigate"
                  class="text-xs uppercase py-3 font-bold block"
                  :class="[
                  isActive
                    ? 'text-emerald-500 hover:text-emerald-600'
                    : 'text-blueGray-700 hover:text-blueGray-500',
                ]"
              >
                <i
                    class="fas fa-tv mr-2 text-sm"
                    :class="[isActive ? 'opacity-75' : 'text-blueGray-300']"
                ></i>
                Dashboard
              </a>
            </router-link>
          </li>

          <li class="items-center">
            <router-link
                to="/event-creator/events"
                v-slot="{ href, navigate, isActive }"
            >
              <a
                  :href="href"
                  @click="navigate"
                  class="text-xs uppercase py-3 font-bold block"
                  :class="[
                  isActive
                    ? 'text-emerald-500 hover:text-emerald-600'
                    : 'text-blueGray-700 hover:text-blueGray-500',
                ]"
              >
                <i
                    class="fas fa-calendar-alt mr-2 text-sm"
                    :class="[isActive ? 'opacity-75' : 'text-blueGray-300']"
                ></i>
                Meine Veranstaltungen
              </a>
            </router-link>
          </li>

          <!-- Veranstaltung erstellen -->
          <li v-if="hasEventCreatorRole" class="items-center">
            <router-link to="/event-creator/create" v-slot="{ href, navigate, isActive }">
              <a :href="href" @click="navigate" class="text-xs uppercase py-3 font-bold block" :class="[isActive ? 'text-emerald-500 hover:text-emerald-600' : 'text-blueGray-700 hover:text-blueGray-500']">
                <i class="fas fa-calendar-alt mr-2 text-sm" :class="[isActive ? 'opacity-75' : 'text-blueGray-300']"></i>
                Veranstaltung erstellen
              </a>
            </router-link>
          </li>
          <!-- Profil -->
          <li class="items-center">
            <router-link
                to="/event-creator/profile"
                v-slot="{ href, navigate, isActive }"
            >
              <a
                  :href="href"
                  @click="navigate"
                  class="text-xs uppercase py-3 font-bold block"
                  :class="[
                  isActive
                    ? 'text-emerald-500 hover:text-emerald-600'
                    : 'text-blueGray-700 hover:text-blueGray-500',
                ]"
              >
                <i
                    class="fas fa-user-circle mr-2 text-sm"
                    :class="[isActive ? 'opacity-75' : 'text-blueGray-300']"
                ></i>
                Profil
              </a>
            </router-link>
          </li>
        </ul>

        <hr class="my-4 md:min-w-full" />
        <ul class="md:flex-col md:min-w-full flex flex-col list-none">
          <li class="items-center">
            <a href="#"
               @click.prevent="handleLogout" class="text-xs uppercase py-3 font-bold block text-red-500 hover:text-red-600">
              <i class="fas fa-sign-out-alt mr-2 text-sm"></i>
              Abmelden
            </a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
import NotificationDropdown from "@/components/Dropdowns/NotificationDropdown.vue";
import UserDropdown from "@/components/Dropdowns/UserDropdown.vue";
import { useAuth } from "@/composables/useAuth";
import router from "@/router";
import {hasEventCreatorRole} from "@/utils/roles";

export default {
  setup() {
    const { logout } = useAuth()

    const handleLogout = async () => {
      try {
        await logout()
        await router.push('/auth/login')
      } catch (error) {
        console.error('Abmeldung fehlgeschlagen:', error)
      }
    }

    return {
      handleLogout,
      hasEventCreatorRole
    }
  },
  data() {
    return {
      collapseShow: "hidden",
    };
  },
  methods: {
    toggleCollapseShow: function (classes) {
      this.collapseShow = classes;
    },
  },
  components: {
    NotificationDropdown,
    UserDropdown,
  },
};
</script>