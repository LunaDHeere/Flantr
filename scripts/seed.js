const admin = require("firebase-admin");
const users = require("./seedData/users.json");
const routes = require("./seedData/routes.json");

admin.initializeApp({
  credential: admin.credential.cert(
    require("./serviceAccountKey.json")
  )
});

const db = admin.firestore();

async function seedUsers() {
  for (const user of users) {
    await db.collection("users").doc(user.id).set({
      ...user,
      memberSince: Date.now()
    });
  }
  console.log("✅ Users seeded");
}

async function seedRoutes() {
  for (const route of routes) {
    const ref = db.collection("routes").doc();
    await ref.set({ ...route, id: ref.id });
  }
  console.log("✅ Routes seeded");
}

async function run() {
  await seedUsers();
  await seedRoutes();
}

run()
  .then(() => process.exit())
  .catch(console.error);
