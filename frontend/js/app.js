const API_URL = 'http://localhost:8080/todos';
const token = localStorage.getItem('token');

let allTodos = []; // 所有数据
let currentFilter = 'all'; //当前筛选

const btnPost = document.querySelector('.btn--post');
const btnDeleteCompleted = document.querySelector('.btn--deleteCompleted');
const logoutBtn = document.getElementById('logout-btn');

const todoList = document.getElementById('todo-list');
const username = localStorage.getItem('username');

///////////////////////////////////////////////////////////////
//todos的函数
//渲染函数

function renderTodos() {
  const list = document.getElementById('todo-list');
  list.innerHTML = '';

  const todos = getFilteredTodos();

  todos.forEach(todo => {
    const li = document.createElement('li');

    li.innerHTML = `
      <label class="todo-item">
        <input 
          type="checkbox" 
          class="todo-checkbox"
          data-id="${todo.id}"
          ${todo.completed ? 'checked' : ''}
        />
        <span class="${todo.completed ? 'completed' : ''}">
          ${todo.title}
        </span>
      </label>

      <div>
        <button class="btn--delete" data-id="${todo.id}">
          删除
        </button>
      </div>
    `;

    list.appendChild(li);
  });
}

//更新筛选UI
function updateFilterUI() {
  document.querySelectorAll('.filters button').forEach(btn => {
    btn.classList.remove('active');
  });

  document.getElementById(`filter-${currentFilter}`).classList.add('active');
}

//筛选任务
function getFilteredTodos() {
  if (currentFilter === 'active') {
    return allTodos.filter(todo => !todo.completed);
  }
  if (currentFilter === 'completed') {
    return allTodos.filter(todo => todo.completed);
  }
  return allTodos;
}

// 获取所有 todo
async function fetchTodos() {
  try {
    const response = await fetch(API_URL, {
      method: 'GET',
      headers: {
        Authorization: 'Bearer ' + token,
      },
    });

    if (!response.ok) {
      throw new Error('获取失败');
    }

    const data = await response.json();
    allTodos = data;
    renderTodos();
  } catch (error) {
    console.error(error);
  }
}

//增加任务
async function addTodoSingle() {
  const input = document.getElementById('todo-input');
  const title = input.value;

  if (!title) return;

  await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + token,
    },
    body: JSON.stringify({
      title: title,
      completed: false,
      userId: 1,
    }),
  });

  input.value = '';
  fetchTodos(); // 重新加载列表
}

//删除任务
async function deleteTodo(id) {
  await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
    headers: {
      Authorization: 'Bearer ' + token,
    },
  });

  fetchTodos(); // 删除后刷新
}

//删除已完成任务
async function deleteCompleteTodo() {
  const response = await fetch(`${API_URL}/completed`, {
    method: 'DELETE',
    headers: {
      Authorization: 'Bearer ' + token,
    },
  });
  const data = await response.json();
  showToast(`delete ${data.deleted} completed todos`);
  fetchTodos(); // 删除后刷新
}

//更新任务
async function toggleTodo(id, completed) {
  await fetch(`${API_URL}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + token,
    },
    body: JSON.stringify({
      completed: !completed,
    }),
  });

  fetchTodos();
}

//提示函数
function showToast(message) {
  const toast = document.getElementById('toast');

  toast.innerText = message;
  toast.style.display = 'block';

  setTimeout(() => {
    toast.style.display = 'none';
    toast.innerText = '';
  }, 2000);
}

///////////////////////////////////////////////////////////////

//判断是否登录
if (!token) {
  window.location.href = 'login.html';
}

//显示用户名
if (username) {
  document.getElementById('username').innerText = username;
}

// 页面加载时渲染当前的任务
fetchTodos();
updateFilterUI();

//按添加按钮或回车键增加任务
btnPost.addEventListener('click', addTodoSingle);
document.addEventListener('keydown', function (e) {
  if (e.key === 'Enter') addTodoSingle();
});

//删除按钮
todoList.addEventListener('click', function (e) {
  const press = e.target;

  if (press.classList.contains('btn--delete')) {
    e.preventDefault();
    deleteTodo(Number(press.dataset.id));
  }
});

//更新任务
todoList.addEventListener('change', function (e) {
  const target = e.target;

  if (target.classList.contains('todo-checkbox')) {
    const id = Number(target.dataset.id);
    const completed = target.checked;

    toggleTodo(id, !completed);
  }
});

//删除所有已完成任务
btnDeleteCompleted.addEventListener('click', deleteCompleteTodo);

//登出按钮
logoutBtn.addEventListener('click', () => {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  window.location.href = 'index.html';
});

//筛选按钮
document.getElementById('filter-all').onclick = () => {
  currentFilter = 'all';
  renderTodos();
  updateFilterUI();
};

document.getElementById('filter-active').onclick = () => {
  currentFilter = 'active';
  renderTodos();
  updateFilterUI();
};

document.getElementById('filter-completed').onclick = () => {
  currentFilter = 'completed';
  renderTodos();
  updateFilterUI();
};
