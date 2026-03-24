// smooth scroll handled by CSS

// scroll spy
const sections = document.querySelectorAll('.section');
const navLinks = document.querySelectorAll('.nav-link');

window.addEventListener('scroll', () => {
  let current = '';

  sections.forEach(section => {
    const sectionTop = section.offsetTop - 120;
    if (pageYOffset >= sectionTop) {
      current = section.getAttribute('id');
    }
  });

  navLinks.forEach(link => {
    link.classList.remove('active');
    if (link.getAttribute('href') === `#${current}`) {
      link.classList.add('active');
    }
  });
});

// fake demo data (结构正确，后面可替换API)
const demoData = ['Finish homework', 'Learn Spring Boot', 'Build Todo App'];

const demoList = document.getElementById('demoList');

demoData.forEach(item => {
  const div = document.createElement('div');
  div.className = 'todo-item';
  div.textContent = '✔ ' + item;
  demoList.appendChild(div);
});

const API_URL = 'http://localhost:8080';

const btnLogin = document.querySelector('.btn--login');
const btnRegiter = document.querySelector('.btn--register');

const switchToRegister = document.querySelector('.switch--toRegister');
const switchToLogin = document.querySelector('.switch--toLogin');

const card = document.querySelector('.card');
const modal = document.getElementById('authModal');

////////////////////////////////////////////////////////

//登录逻辑
async function login(username, password) {
  try {
    const response = await fetch(`${API_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username,
        password: password,
      }),
    });

    if (!response.ok) {
      throw new Error('登录失败');
    }

    const data = await response.json();

    // 保存 token
    localStorage.setItem('token', data.token);
    localStorage.setItem('username', data.username);

    // 跳转页面
    window.location.href = 'app.html';
  } catch (error) {
    alert(error.message);
  }
}

//注册逻辑
async function register(username, password) {
  try {
    const response = await fetch(`${API_URL}/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username,
        password: password,
      }),
    });

    if (!response.ok) {
      throw new Error('注册失败');
    }

    const data = await response.json();

    alert('注册成功');

    card.classList.remove('show-register');
  } catch (error) {
    alert(error.message);
  }
}

//登陆函数
const loginFunction = async () => {
  const usernameLogin = document.getElementById('username--login').value;
  const passwordLogin = document.getElementById('password--login').value;

  if (!usernameLogin || !passwordLogin) {
    alert('不能为空');
    return;
  }

  await login(usernameLogin, passwordLogin);
};

////////////////////////////////////////////////////////

//登录按钮
btnLogin.addEventListener('click', loginFunction);
document.addEventListener('keydown', function (e) {
  if (e.key === 'Enter') loginFunction();
});

//注册按钮
btnRegiter.addEventListener('click', async () => {
  const usernameRegister = document.getElementById('username--register').value;
  const passwordRegister = document.getElementById('password--register').value;

  if (!usernameRegister || !passwordRegister) {
    alert('不能为空');
    return;
  }

  if (passwordRegister.length < 6) {
    alert('密码长度不能小于6');
    return;
  }

  await register(usernameRegister, passwordRegister);
});

//切换到注册界面
switchToRegister.addEventListener('click', function (e) {
  card.classList.add('show-register');
});

//切换到登陆界面
switchToLogin.addEventListener('click', function (e) {
  card.classList.remove('show-register');
});

// Get Started → 注册面板
document.querySelectorAll('.btn.primary').forEach(function (btn) {
  btn.addEventListener('click', () => {
    modal.classList.remove('hidden');
    card.classList.add('show-register');
  });
});

// Login → 登录面板
document.querySelectorAll('.btn.outline').forEach(function (btn) {
  btn.addEventListener('click', () => {
    modal.classList.remove('hidden');
    card.classList.remove('show-register');
  });
});

document.querySelector('.nav--login').addEventListener('click', () => {
  modal.classList.remove('hidden');
  card.classList.remove('show-register');
});

// 关闭
document.getElementById('closeModal').addEventListener('click', () => {
  modal.classList.add('hidden');
});
document.querySelector('.modal').addEventListener('click', e => {
  if (e.target === e.currentTarget) {
    modal.classList.add('hidden');
  }
});
